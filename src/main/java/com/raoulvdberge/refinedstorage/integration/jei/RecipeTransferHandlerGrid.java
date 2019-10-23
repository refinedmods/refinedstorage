package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.network.MessageGridProcessingTransfer;
import com.raoulvdberge.refinedstorage.network.MessageGridTransfer;
import mezz.jei.api.IRecipesGui;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeTransferHandlerGrid implements IRecipeTransferHandler<ContainerGrid> {
    public static final long TRANSFER_SCROLL_DELAY_MS = 200;
    public static long LAST_TRANSFER;

    private static final IRecipeTransferError ERROR_CANNOT_TRANSFER = new IRecipeTransferError() {
        @Override
        public Type getType() {
            return Type.INTERNAL;
        }

        @Override
        public void showError(Minecraft minecraft, int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {
            // NO OP
        }
    };
    private IRecipeTransferHandlerHelper handlerHelper;

    public RecipeTransferHandlerGrid(IRecipeTransferHandlerHelper handlerHelper) {
        this.handlerHelper = handlerHelper;
    }

    @Override
    public Class<ContainerGrid> getContainerClass() {
        return ContainerGrid.class;
    }

    @Override
    public IRecipeTransferError transferRecipe(ContainerGrid container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        IGrid grid = container.getGrid();
        GridType gridType = grid.getGridType();

        if (gridType != GridType.CRAFTING && gridType != GridType.PATTERN) {
            return ERROR_CANNOT_TRANSFER;
        }

        boolean isProcessingPattern = gridType == GridType.PATTERN && ((NetworkNodeGrid) grid).isProcessingPattern();
        boolean isCraftingRecipe = recipeLayout.getRecipeCategory().getUid().equals(VanillaRecipeCategoryUid.CRAFTING);

        if (gridType == GridType.PATTERN) {
            // will be removed in #2316
            if (isProcessingPattern && isCraftingRecipe) {
                return this.handlerHelper.createUserErrorWithTooltip(I18n.format("gui.refinedstorage:jei.tooltip.error.recipe.transfer.pattern.turn_off_processing"));
            }
            if (!isProcessingPattern && !isCraftingRecipe) {
                return this.handlerHelper.createUserErrorWithTooltip(I18n.format("gui.refinedstorage:jei.tooltip.error.recipe.transfer.pattern.turn_on_processing"));
            }
        } else { // gridType == GridType.CRAFTING
            if (!isCraftingRecipe) {
                return ERROR_CANNOT_TRANSFER;
            }

            // Check whether we have enough items to craft the recipe and return an error if we don't.
            // To get the items grid, we get the grid's GUI from an internal JEI method and use its GridView.
            // There is no other (synchronous) way of retrieving a list of items in the grid.

            // This should try to replicate the behaviour of NetworkNodeGrid#onRecipeTransfer.
            // Assume there is enough space in the network to clear the crafting matrix - there's no way of determining
            // this without access to the network itself.
            IRecipesGui recipesGui = RSJEIPlugin.INSTANCE.getRuntime().getRecipesGui();
            try {
                Object parentScreen = recipesGui.getClass().getMethod("getParentScreen").invoke(recipesGui);
                if (parentScreen instanceof GuiGrid) {
                    // Using IGridView#getStacks will return a *filtered* list of items in the view,
                    // which will cause problems - especially if the user uses JEI synchronised searching.
                    // Instead, we will use IGridView#getAllStacks which provides an unordered view of all GridStacks.
                    Collection<IGridStack> gridStacks = ((GuiGrid) parentScreen).getView().getAllStacks();

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
                        if (!(gridStack instanceof GridStackItem)) {
                            continue;
                        }
                        if (!ingredientsToSlotsToStacks.containsKey(((GridStackItem) gridStack).getStack().getItem())) {
                            continue;
                        }
                        GridStackItem gridStackItem = (GridStackItem) gridStack;
                        ItemStack stack = gridStackItem.getStack();
                        Item item = stack.getItem();
                        List<List<ItemStack>> slots = ingredientsToSlotsToStacks.get(item);

                        int available = gridStackItem.getQuantity();
                        int compareDamage = item.isDamageable() ? 0 : IComparer.COMPARE_DAMAGE;
                        for (int i = 0; i < 9; i++) {
                            if (isFound[i]) {
                                continue;
                            }
                            for (ItemStack possibility : slots.get(i)) {
                                if (comparer.isEqual(possibility, stack, IComparer.COMPARE_NBT | compareDamage)) {
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
                        int compareDamage = item.isDamageable() ? 0 : IComparer.COMPARE_DAMAGE;
                        for (int i = 0; i < 9; i++) {
                            if (isFound[i]) {
                                continue;
                            }
                            for (ItemStack possibility : slots.get(i)) {
                                if (comparer.isEqual(possibility, stack, IComparer.COMPARE_NBT | compareDamage)) {
                                    if (possibility.getCount() <= available) {
                                        isFound[i] = true;
                                        available -= possibility.getCount();
                                        break;
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
                                missingAutocraftItems.add(i+1);
                            } else {
                                missingItems.add(i+1);
                            }
                        }
                    }
                    if (!missingAutocraftItems.isEmpty()) {
                        return new RecipeTransferErrorAutocraftSlots(
                                I18n.format("jei.tooltip.error.recipe.transfer.missing"),
                                I18n.format("gui.refinedstorage:jei.tooltip.error.recipe.transfer.missing.autocraft"),
                                missingItems,
                                missingAutocraftItems);
                    } else if (!missingItems.isEmpty()) {
                        return this.handlerHelper.createUserErrorForSlots(I18n.format("jei.tooltip.error.recipe.transfer.missing"), missingItems);
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (doTransfer) {
            LAST_TRANSFER = System.currentTimeMillis();

            if (isProcessingPattern) {
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

                RS.INSTANCE.network.sendToServer(new MessageGridProcessingTransfer(inputs, outputs, fluidInputs, fluidOutputs));
            } else {
                RS.INSTANCE.network.sendToServer(new MessageGridTransfer(recipeLayout.getItemStacks().getGuiIngredients(), container.inventorySlots.stream().filter(s -> s.inventory instanceof InventoryCrafting).collect(Collectors.toList())));
            }
        }

        return null;
    }
}
