package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.network.grid.GridProcessingTransferMessage;
import com.refinedmods.refinedstorage.network.grid.GridTransferMessage;
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

public class GridRecipeTransferHandler implements IRecipeTransferHandler {
    public static final long TRANSFER_SCROLLBAR_DELAY_MS = 200;
    public static long LAST_TRANSFER_TIME;
    IRecipeTransferHandlerHelper transferHelper;

    public GridRecipeTransferHandler(IRecipeTransferHandlerHelper transferHelper) {
        this.transferHelper = transferHelper;
    }

    @Override
    public Class<? extends Container> getContainerClass() {
        return GridContainer.class;
    }

    @Override
    public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
        IGrid grid = ((GridContainer) container).getGrid();
        // Check whether we have enough items to craft the recipe and return an error if we don't.
        // To get the items grid, we get the grid's GUI from an internal JEI method and use its GridView.
        // There is no other (synchronous) way of retrieving a list of items in the grid.

        // This should try to replicate the behaviour of NetworkNodeGrid#onRecipeTransfer.
        // Assume there is enough space in the network to clear the crafting matrix - there's no way of determining
        // this without access to the network itself.
        if (isCraftingRecipe(recipeLayout.getRecipeCategory())) {
            IScreenInfoProvider parentScreen = ((GridContainer) container).getScreenInfoProvider();
            if (parentScreen instanceof GridScreen) {

                // Using IGridView#getStacks will return a *filtered* list of items in the view,
                // which will cause problems - especially if the user uses JEI synchronised searching.
                // Instead, we will use IGridView#getAllStacks which provides an unordered view of all GridStacks.
                Collection<IGridStack> gridStacks = ((GridScreen) parentScreen).getView().getAllStacks();

                boolean[] isFound = new boolean[9];
                boolean[] canAutocraft = new boolean[9];
                // Map from Items to a 9-long array of slots, where each slot is an array of ItemStacks which is
                // valid in that slot of the recipe.
                Map<Item, List<List<ItemStack>>> ingredientsToSlotsToStacks = new HashMap<>();

                Map<Integer, ? extends IGuiIngredient<ItemStack>> guiIngredients = recipeLayout.getItemStacks().getGuiIngredients();

                // Initialise variables
                for (int i = 0; i < 9; i++) {
                    isFound[i] = true;
                    List<ItemStack> ingredients = guiIngredients.get(i + 1).getAllIngredients();
                    for (ItemStack ingredient : ingredients) {
                        if (ingredient == null) {
                            continue;
                        }
                        isFound[i] = false;
                        Item item = ingredient.getItem();
                        if (!ingredientsToSlotsToStacks.containsKey(item)) {
                            List<List<ItemStack>> slots = new ArrayList<>(9);
                            for (int j = 0; j < 9; j++) {
                                slots.add(new ArrayList<>());
                            }
                            ingredientsToSlotsToStacks.put(item, slots);
                        }
                        ingredientsToSlotsToStacks.get(item).get(i).add(ingredient);
                    }
                }

                // Check grid
                IComparer comparer = API.instance().getComparer();

                for (IGridStack gridStack : gridStacks) {
                    if (!(gridStack instanceof ItemGridStack)) {
                        continue;
                    }
                    if (!ingredientsToSlotsToStacks.containsKey(((ItemGridStack) gridStack).getStack().getItem())) {
                        continue;
                    }
                    ItemGridStack gridStackItem = (ItemGridStack) gridStack;
                    ItemStack stack = gridStackItem.getStack();
                    Item item = stack.getItem();
                    List<List<ItemStack>> slots = ingredientsToSlotsToStacks.get(item);

                    int available = gridStackItem.getQuantity();
                    for (int i = 0; i < 9; i++) {
                        if (isFound[i]) {
                            continue;
                        }
                        for (ItemStack possibility : slots.get(i)) {
                            if (comparer.isEqual(possibility, stack, IComparer.COMPARE_NBT)) {
                                if (possibility.getCount() <= available) {
                                    isFound[i] = true;
                                    available -= possibility.getCount();
                                    break;
                                } else if (gridStackItem.isCraftable()) {
                                    canAutocraft[i] = true;
                                }
                            }
                        }
                    }
                }

                // Check inventory
                for (int inventorySlot = 0; inventorySlot < player.inventory.getSizeInventory(); inventorySlot++) {
                    if (!ingredientsToSlotsToStacks.containsKey(player.inventory.getStackInSlot(inventorySlot).getItem())) {
                        continue;
                    }
                    ItemStack stack = player.inventory.getStackInSlot(inventorySlot);
                    Item item = stack.getItem();
                    List<List<ItemStack>> slots = ingredientsToSlotsToStacks.get(item);

                    int available = stack.getCount();
                    for (int i = 0; i < 9; i++) {
                        if (isFound[i]) {
                            continue;
                        }
                        for (ItemStack possibility : slots.get(i)) {
                            if (comparer.isEqual(possibility, stack, IComparer.COMPARE_NBT)) {
                                if (possibility.getCount() <= available) {
                                    isFound[i] = true;
                                    available -= possibility.getCount();
                                    break;
                                }
                            }
                        }
                    }
                }
                // Check grid crafting slots
                CraftingInventory craftingMatrix = grid.getCraftingMatrix();
                if (craftingMatrix != null) {
                    for (int matrixSlot = 0; matrixSlot < craftingMatrix.getSizeInventory(); matrixSlot++) {
                        if (!ingredientsToSlotsToStacks.containsKey(craftingMatrix.getStackInSlot(matrixSlot).getItem())) {
                            continue;
                        }
                        ItemStack stack = craftingMatrix.getStackInSlot(matrixSlot);

                        Item item = stack.getItem();
                        List<List<ItemStack>> slots = ingredientsToSlotsToStacks.get(item);

                        int available = stack.getCount();
                        for (int i = 0; i < 9; i++) {
                            if (isFound[i]) {
                                continue;
                            }
                            for (ItemStack possibility : slots.get(i)) {
                                if (comparer.isEqual(possibility, stack, IComparer.COMPARE_NBT)) {
                                    if (possibility.getCount() <= available) {
                                        isFound[i] = true;
                                        available -= possibility.getCount();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                // Generate an appropriate error if necessary
                List<Integer> missingItems = new ArrayList<>();
                List<Integer> missingAutocraftItems = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    if (!isFound[i]) {
                        if (canAutocraft[i]) {
                            missingAutocraftItems.add(i + 1);
                        } else {
                            missingItems.add(i + 1);
                        }
                    }
                }
                if (!missingAutocraftItems.isEmpty()) {
                    return new com.refinedmods.refinedstorage.integration.jei.RecipeTransferErrorAutocraftSlots(
                        I18n.format("jei.tooltip.error.recipe.transfer.missing"),
                        I18n.format("gui.refinedstorage:jei.tooltip.error.recipe.transfer.missing.autocraft"),
                        missingItems,
                        missingAutocraftItems);
                } else if (!missingItems.isEmpty()) {
                    return this.transferHelper.createUserErrorForSlots(I18n.format("jei.tooltip.error.recipe.transfer.missing"), missingItems);
                }
            }
        }

        if (doTransfer) {
            LAST_TRANSFER_TIME = System.currentTimeMillis();

            if (grid.getGridType() == GridType.PATTERN && !isCraftingRecipe(recipeLayout.getRecipeCategory())) {
                List<ItemStack> inputs = new LinkedList<>();
                List<ItemStack> outputs = new LinkedList<>();

                List<FluidStack> fluidInputs = new LinkedList<>();
                List<FluidStack> fluidOutputs = new LinkedList<>();

                for (IGuiIngredient<ItemStack> guiIngredient : recipeLayout.getItemStacks().getGuiIngredients().values()) {
                    if (guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
                        ItemStack ingredient = guiIngredient.getDisplayedIngredient().copy();

                        if (guiIngredient.isInput()) {
                            inputs.add(ingredient);
                        } else {
                            outputs.add(ingredient);
                        }
                    }
                }

                for (IGuiIngredient<FluidStack> guiIngredient : recipeLayout.getFluidStacks().getGuiIngredients().values()) {
                    if (guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
                        FluidStack ingredient = guiIngredient.getDisplayedIngredient().copy();

                        if (guiIngredient.isInput()) {
                            fluidInputs.add(ingredient);
                        } else {
                            fluidOutputs.add(ingredient);
                        }
                    }
                }

                RS.NETWORK_HANDLER.sendToServer(new GridProcessingTransferMessage(inputs, outputs, fluidInputs, fluidOutputs));
            } else {
                RS.NETWORK_HANDLER.sendToServer(new GridTransferMessage(
                    recipeLayout.getItemStacks().getGuiIngredients(),
                    container.inventorySlots.stream().filter(s -> s.inventory instanceof CraftingInventory).collect(Collectors.toList())
                ));
            }
        }

        return null;
    }

    private boolean isCraftingRecipe(IRecipeCategory<?> recipeCategory) {
        return recipeCategory.getUid().equals(VanillaRecipeCategoryUid.CRAFTING);
    }
}
