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
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
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

            return null;
        }

        // Check whether we have enough items to craft the recipe and return an error if we don't.
        // To get the items grid, we get the grid's GUI from an internal JEI method and use its GridView.
        // There is no other (synchronous) way of retrieving a list of items in the grid.
        IScreenInfoProvider gridSreen = ((GridContainer) container).getScreenInfoProvider();
        if (gridSreen instanceof GridScreen) {
            // Using IGridView#getStacks will return a *filtered* list of items in the view,
            // which will cause problems - especially if the user uses JEI synchronised searching.
            // Instead, we will use IGridView#getAllStacks which provides an unordered view of all GridStacks.
            Collection<IGridStack> gridStacks = ((GridScreen) gridSreen).getView().getAllStacks();
            List<GuiIngredientAvailability> ingredients = new ArrayList<>();
            List<GuiIngredientAvailability> ingredientsFluids = new ArrayList<>();

            //Initialise ingredients
            for (Map.Entry<Integer, ? extends IGuiIngredient<ItemStack>> entry : recipeLayout.getItemStacks().getGuiIngredients().entrySet()) {
                if (entry.getValue().isInput() && !entry.getValue().getAllIngredients().isEmpty()) {
                    ingredients.add(new GuiIngredientAvailability<>(entry.getValue()));
                }
            }
            for (Map.Entry<Integer, ? extends IGuiIngredient<FluidStack>> entry : recipeLayout.getFluidStacks().getGuiIngredients().entrySet()) {
                if (entry.getValue().isInput() && !entry.getValue().getAllIngredients().isEmpty()) {
                    ingredientsFluids.add(new GuiIngredientAvailability<>(entry.getValue()));
                }
            }

            // Check grid
            for (IGridStack gridStack : gridStacks) {
                if ((gridStack instanceof ItemGridStack)) {
                    checkIfStackIsNeeded(ingredients, ((ItemGridStack) gridStack).getStack(), gridStack.isCraftable());
                } else if (gridStack instanceof FluidGridStack) {
                    checkIfStackIsNeeded(ingredientsFluids, ((FluidGridStack) gridStack).getStack(), gridStack.isCraftable());
                }

            }

            // Check inventory
            for (int inventorySlot = 0; inventorySlot < player.inventory.getSizeInventory(); inventorySlot++) {
                if(!player.inventory.getStackInSlot(inventorySlot).isEmpty()){
                    checkIfStackIsNeeded(ingredients, player.inventory.getStackInSlot(inventorySlot), false);
                }
            }

            // Check grid crafting slots
            if (grid.getGridType().equals(GridType.CRAFTING)) {
                CraftingInventory craftingMatrix = grid.getCraftingMatrix();
                if (craftingMatrix != null) {
                    for (int matrixSlot = 0; matrixSlot < craftingMatrix.getSizeInventory(); matrixSlot++) {
                        if(!craftingMatrix.getStackInSlot(matrixSlot).isEmpty()){
                            checkIfStackIsNeeded(ingredients, craftingMatrix.getStackInSlot(matrixSlot), false);
                        }
                    }
                }
            }
            if (ingredients.stream().anyMatch(x -> !x.isAvailable())||ingredientsFluids.stream().anyMatch(x->!x.isAvailable())) {
                return new RecipeTransferGridError(grid.getGridType(), ingredients, ingredientsFluids);
            }
        }

        return null;
    }

    private boolean isCraftingRecipe(IRecipeCategory<?> recipeCategory) {
        return recipeCategory.getUid().equals(VanillaRecipeCategoryUid.CRAFTING);
    }

    @SuppressWarnings("rawtypes")
    private void checkIfStackIsNeeded(List<GuiIngredientAvailability> ingredients, Object stack, boolean isCraftable) {
        int available;
        boolean isItem = stack instanceof ItemStack;
        if (stack instanceof ItemStack) {
            available = ((ItemStack) stack).getCount();
        } else if (stack instanceof FluidStack) {
            available = ((FluidStack) stack).getAmount();
        } else {
            return;
        }
        for (GuiIngredientAvailability GIA : ingredients) {
            if(available == 0){
                return;
            }
            if (GIA.isAvailable()) {
                continue;
            }
            Optional match = GIA.guiIngredient.getAllIngredients().stream().filter(x -> {
                if (isItem) {
                    return API.instance().getComparer().isEqual((ItemStack) stack, (ItemStack) x, IComparer.COMPARE_NBT);
                } else {
                    return API.instance().getComparer().isEqual((FluidStack) stack, (FluidStack) x, IComparer.COMPARE_NBT);
                }
            }).findFirst();
            if (!match.isPresent()) {
                continue;
            } else if (isCraftable) {
                GIA.isCraftable = true;
            } else { //crafables and non craftables are 2 different gridstacks
                int needed = Math.min(GIA.required - GIA.fulfilled, Math.min(available, isItem ? ((ItemStack) match.get()).getCount() : ((FluidStack) match.get()).getAmount()));
                GIA.fulfilled += needed;
                available -= needed;
            }
        }
    }

    public static class GuiIngredientAvailability<T> {
        IGuiIngredient<T> guiIngredient;
        boolean isCraftable;
        int required;
        int fulfilled;

        public GuiIngredientAvailability(IGuiIngredient<T> guiIngredient) {
            this.guiIngredient = guiIngredient;
            Object stack;
            stack = guiIngredient.getAllIngredients().get(0);
            if (stack instanceof ItemStack) {
                required = ((ItemStack) stack).getCount();
            } else if (stack instanceof FluidStack) {
                required = ((FluidStack) stack).getAmount();
            }
        }

        public boolean isAvailable() {
            return required == fulfilled;
        }

        public boolean isCraftable() {
            return isCraftable;
        }
    }
}
