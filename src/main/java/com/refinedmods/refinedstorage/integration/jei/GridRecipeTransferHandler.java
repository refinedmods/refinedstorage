package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewRequestMessage;
import com.refinedmods.refinedstorage.network.grid.GridProcessingTransferMessage;
import com.refinedmods.refinedstorage.network.grid.GridTransferMessage;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class GridRecipeTransferHandler implements IRecipeTransferHandler<GridContainerMenu, Object> {
    public static final GridRecipeTransferHandler INSTANCE = new GridRecipeTransferHandler();

    private static final long TRANSFER_SCROLLBAR_DELAY_MS = 200;

    private long lastTransferTimeMs;

    private final Map<IRecipeSlotsView, IngredientTracker> trackerCache = new HashMap<>();

    public final void cleanTrackerCache() {
        trackerCache.clear();
    }

    private GridRecipeTransferHandler() {
    }

    @Override
    public Class<GridContainerMenu> getContainerClass() {
        return GridContainerMenu.class;
    }

    @Override
    public Optional<MenuType<GridContainerMenu>> getMenuType() {
        return Optional.of(RSContainerMenus.GRID.get());
    }

    @Override
    public RecipeType<Object> getRecipeType() {
        // This is not actually used, as we register with JEI as a universal handler
        return null;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(GridContainerMenu container, Object recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (!(container.getScreenInfoProvider() instanceof GridScreen)) {
            return null;
        }
        GridType type = container.getGrid().getGridType();

        if (type == GridType.CRAFTING) {
            return transferRecipeForCraftingGrid(container, recipe, recipeSlots, player, doTransfer);
        } else if (type == GridType.PATTERN) {
            return transferRecipeForPatternGrid(container, recipe, recipeSlots, player, doTransfer);
        }

        return null;
    }

    private RecipeTransferCraftingGridError transferRecipeForCraftingGrid(GridContainerMenu container, Object recipe, IRecipeSlotsView recipeLayout, Player player, boolean doTransfer) {
        IngredientTracker tracker = createTracker(container, recipeLayout, player, doTransfer);

        if (doTransfer) {
            if (tracker.hasMissingButAutocraftingAvailable() && Screen.hasControlDown()) {
                tracker.createCraftingRequests().forEach((id, count) -> RS.NETWORK_HANDLER.sendToServer(
                    new GridCraftingPreviewRequestMessage(
                        id,
                        count,
                        Screen.hasShiftDown(),
                        false
                    )
                ));
            } else {
                moveItems(container, recipe, recipeLayout, tracker);
            }
        } else {
            if (tracker.hasMissing()) {
                return new RecipeTransferCraftingGridError(tracker);
            }
        }

        return null;
    }

    private IRecipeTransferError transferRecipeForPatternGrid(GridContainerMenu container, Object recipe, IRecipeSlotsView recipeLayout, Player player, boolean doTransfer) {
        IngredientTracker tracker = createTracker(container, recipeLayout, player, doTransfer);

        if (doTransfer) {
            moveItems(container, recipe, recipeLayout, tracker);
        } else {
            if (tracker.isAutocraftingAvailable()) {
                return new RecipeTransferPatternGridError(tracker);
            }
        }

        return null;
    }

    private IngredientTracker createTracker(GridContainerMenu container, IRecipeSlotsView recipeLayout, Player player, boolean doTransfer) {
        {
            IngredientTracker old = trackerCache.get(recipeLayout);

            if (old != null) {
                return old;
            }
        }

        IngredientTracker tracker = new IngredientTracker(recipeLayout, doTransfer);

        // Check grid
        if (container.getGrid().isGridActive()) {
            // Using IGridView#getStacks will return a *filtered* list of items in the view,
            // which will cause problems - especially if the user uses JEI synchronised searching.
            // Instead, we will use IGridView#getAllStacks which provides an unordered view of all GridStacks.
            Collection<IGridStack> gridStacks = ((GridScreen) container.getScreenInfoProvider()).getView().getAllStacks();

            for (IGridStack gridStack : gridStacks) {
                if (gridStack instanceof ItemGridStack itemGridStack) {
                    tracker.addAvailableStack(itemGridStack.getStack(), gridStack);
                }
            }
        }

        // Check inventory
        {
            Inventory playerInventory = player.getInventory();

            for (int inventorySlot = 0; inventorySlot < playerInventory.getContainerSize(); inventorySlot++) {
                ItemStack itemStack = playerInventory.getItem(inventorySlot);

                if (!itemStack.isEmpty()) {
                    tracker.addAvailableStack(itemStack, null);
                }
            }
        }

        // Check grid crafting slots
        {
            IGrid grid = container.getGrid();

            if (grid.getGridType().equals(GridType.CRAFTING)) {
                CraftingContainer craftingMatrix = grid.getCraftingMatrix();

                if (craftingMatrix != null) {
                    for (int matrixSlot = 0; matrixSlot < craftingMatrix.getContainerSize(); matrixSlot++) {
                        ItemStack itemStack = craftingMatrix.getItem(matrixSlot);

                        if (!itemStack.isEmpty()) {
                            tracker.addAvailableStack(itemStack, null);
                        }
                    }
                }
            }
        }

        trackerCache.put(recipeLayout, tracker);

        return tracker;
    }

    public boolean hasTransferredRecently() {
        return System.currentTimeMillis() - lastTransferTimeMs <= TRANSFER_SCROLLBAR_DELAY_MS;
    }

    private void moveItems(GridContainerMenu gridContainer, Object recipe, IRecipeSlotsView recipeLayout, IngredientTracker tracker) {
        this.lastTransferTimeMs = System.currentTimeMillis();

        if (gridContainer.getGrid().getGridType() == GridType.PATTERN && !(recipe instanceof CraftingRecipe)) {
            moveForProcessing(recipeLayout, tracker);
        } else {
            move(recipeLayout);
        }
    }

    private void move(IRecipeSlotsView recipeSlotsView) {
        List<List<ItemStack>> inputs = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).stream().map(view -> {

            //Creating a mutable list
            List<ItemStack> stacks = view.getIngredients(VanillaTypes.ITEM_STACK).collect(Collectors.toCollection(ArrayList::new));

            //moving the displayed stack to first
            Optional<ItemStack> displayStack = view.getDisplayedIngredient(VanillaTypes.ITEM_STACK);
            displayStack.ifPresent(stack -> {
                int index = stacks.indexOf(stack);
                if (index > -1) {
                    stacks.remove(index);
                    stacks.add(0, stack);
                }
            });
            return stacks;
        }).toList();

        RS.NETWORK_HANDLER.sendToServer(new GridTransferMessage(inputs));
    }

    private void moveForProcessing(IRecipeSlotsView recipeLayout, IngredientTracker tracker) {
        List<ItemStack> inputs = new LinkedList<>();
        List<ItemStack> outputs = new LinkedList<>();

        List<FluidStack> fluidInputs = new LinkedList<>();
        List<FluidStack> fluidOutputs = new LinkedList<>();

        List<IRecipeSlotView> inputSlots = recipeLayout.getSlotViews(RecipeIngredientRole.INPUT);
        for (IRecipeSlotView view : inputSlots) {
            handleItemIngredient(inputs, view, tracker);
            handleFluidIngredient(fluidInputs, view);
        }

        List<IRecipeSlotView> outputSlots = recipeLayout.getSlotViews(RecipeIngredientRole.OUTPUT);
        for (IRecipeSlotView view : outputSlots) {
            handleItemIngredient(outputs, view, tracker);
            handleFluidIngredient(fluidOutputs, view);
        }

        RS.NETWORK_HANDLER.sendToServer(new GridProcessingTransferMessage(inputs, outputs, fluidInputs, fluidOutputs));
    }

    private void handleFluidIngredient(List<FluidStack> list, IRecipeSlotView slotView) {
        if (slotView != null) {
            slotView.getDisplayedIngredient(ForgeTypes.FLUID_STACK).ifPresent(list::add);
        }
    }

    private void handleItemIngredient(List<ItemStack> list, IRecipeSlotView slotView, IngredientTracker tracker) {
        if (slotView != null && slotView.getIngredients(VanillaTypes.ITEM_STACK).findAny().isPresent()) {
            ItemStack stack = tracker.findBestMatch(slotView.getIngredients(VanillaTypes.ITEM_STACK).toList());

            if (stack.isEmpty() && slotView.getDisplayedIngredient(VanillaTypes.ITEM_STACK).isPresent()) {
                stack = slotView.getDisplayedIngredient(VanillaTypes.ITEM_STACK).get();
            }
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
    }
}
