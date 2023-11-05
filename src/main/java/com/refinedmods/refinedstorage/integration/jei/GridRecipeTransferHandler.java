package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewRequestMessage;
import com.refinedmods.refinedstorage.network.grid.GridProcessingTransferMessage;
import com.refinedmods.refinedstorage.network.grid.GridTransferMessage;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GridRecipeTransferHandler implements IRecipeTransferHandler<GridContainerMenu, Object> {
    public static final GridRecipeTransferHandler INSTANCE = new GridRecipeTransferHandler();

    private static final long TRANSFER_SCROLLBAR_DELAY_MS = 200;

    private long lastTransferTimeMs;

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
        if (!(container.getScreenInfoProvider() instanceof GridScreen gridScreen)) {
            return null;
        }

        //When JEI is open the screen no longer ticks and doesn't run Actions. However, we do still want to run the actions that update the grid to keep the stored items up to date
        gridScreen.runActions();

        Ingredient.IngredientList ingredientList = new Ingredient.IngredientList();
        for (IRecipeSlotView slotView : recipeSlots.getSlotViews(RecipeIngredientRole.INPUT)) {
            Optional<ItemStack> firstStack = slotView.getItemStacks().findAny();
            ingredientList.add(new Ingredient(slotView, firstStack.map(ItemStack::getCount).orElse(0)));
        }

        IngredientTracker tracker = IngredientTracker.getTracker(container);
        tracker.updateAvailability(ingredientList, container, player);

        GridType type = container.getGrid().getGridType();

        if (type == GridType.CRAFTING) {
            return transferRecipeForCraftingGrid(container, recipe, recipeSlots, player, doTransfer, ingredientList);
        } else if (type == GridType.PATTERN) {
            return transferRecipeForPatternGrid(container, recipe, recipeSlots, player, doTransfer, ingredientList);
        }

        return null;
    }

    private RecipeTransferCraftingGridError transferRecipeForCraftingGrid(GridContainerMenu container, Object recipe, IRecipeSlotsView recipeLayout, Player player, boolean doTransfer, Ingredient.IngredientList ingredientList) {

        if (doTransfer) {
            if (ingredientList.hasMissingButAutocraftingAvailable() && Screen.hasControlDown()) {
                ingredientList.createCraftingRequests().forEach((id, count) -> RS.NETWORK_HANDLER.sendToServer(
                    new GridCraftingPreviewRequestMessage(
                        id,
                        count,
                        Screen.hasShiftDown(),
                        false
                    )
                ));
            } else {
                moveItems(container, recipe, recipeLayout, player);
            }
        } else {
            if (ingredientList.hasMissing()) {
                return new RecipeTransferCraftingGridError(ingredientList);
            }
        }

        return null;
    }

    private IRecipeTransferError transferRecipeForPatternGrid(GridContainerMenu container, Object recipe, IRecipeSlotsView recipeLayout, Player player, boolean doTransfer, Ingredient.IngredientList ingredientList) {
        if (doTransfer) {
            moveItems(container, recipe, recipeLayout, player);
        } else {
            if (ingredientList.isAutocraftingAvailable()) {
                return new RecipeTransferPatternGridError(ingredientList);
            }
        }

        return null;
    }

    public boolean hasTransferredRecently() {
        return System.currentTimeMillis() - lastTransferTimeMs <= TRANSFER_SCROLLBAR_DELAY_MS;
    }

    private void moveItems(GridContainerMenu gridContainer, Object recipe, IRecipeSlotsView recipeLayout, Player player) {
        this.lastTransferTimeMs = System.currentTimeMillis();

        boolean isCraftingRecipe = false;
        if(recipe instanceof Recipe<?> castRecipe)
        {
            isCraftingRecipe = castRecipe.getType() == net.minecraft.world.item.crafting.RecipeType.CRAFTING;
        }

        if (gridContainer.getGrid().getGridType() == GridType.PATTERN && !isCraftingRecipe) {
            moveForProcessing(recipeLayout, gridContainer, player);
        } else {
            move(recipeLayout);
        }
    }

    private void move(IRecipeSlotsView recipeSlotsView) {
        List<List<ItemStack>> inputs = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).stream().map(view -> {

            //Creating a mutable list
            List<ItemStack> stacks = view.getItemStacks().collect(Collectors.toCollection(ArrayList::new));

            //moving the displayed stack to first
            Optional<ItemStack> displayStack = view.getDisplayedItemStack();
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

    private void moveForProcessing(IRecipeSlotsView recipeLayout, GridContainerMenu gridContainer, Player player) {
        List<ItemStack> inputs = new LinkedList<>();
        List<ItemStack> outputs = new LinkedList<>();

        List<FluidStack> fluidInputs = new LinkedList<>();
        List<FluidStack> fluidOutputs = new LinkedList<>();

        List<IRecipeSlotView> inputSlots = recipeLayout.getSlotViews(RecipeIngredientRole.INPUT);
        for (IRecipeSlotView view : inputSlots) {
            handleItemIngredient(inputs, view, gridContainer, player);
            handleFluidIngredient(fluidInputs, view);
        }

        List<IRecipeSlotView> outputSlots = recipeLayout.getSlotViews(RecipeIngredientRole.OUTPUT);
        for (IRecipeSlotView view : outputSlots) {
            handleItemIngredient(outputs, view, gridContainer, player);
            handleFluidIngredient(fluidOutputs, view);
        }

        RS.NETWORK_HANDLER.sendToServer(new GridProcessingTransferMessage(inputs, outputs, fluidInputs, fluidOutputs));
    }

    private void handleFluidIngredient(List<FluidStack> list, IRecipeSlotView slotView) {
        if (slotView != null) {
            slotView.getDisplayedIngredient(ForgeTypes.FLUID_STACK).ifPresent(list::add);
        }
    }

    private void handleItemIngredient(List<ItemStack> list, IRecipeSlotView slotView, GridContainerMenu gridContainer, Player player) {
        if (slotView != null && slotView.getItemStacks().findAny().isPresent()) {
            ItemStack stack = IngredientTracker.getTracker(gridContainer).findBestMatch(gridContainer, player, slotView.getItemStacks().toList());

            if (stack.isEmpty() && slotView.getDisplayedItemStack().isPresent()) {
                stack = slotView.getDisplayedItemStack().get();
            }
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
    }
}
