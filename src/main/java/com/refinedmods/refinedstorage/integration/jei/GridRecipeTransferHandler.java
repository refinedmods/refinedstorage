package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.network.grid.GridProcessingTransferMessage;
import com.refinedmods.refinedstorage.network.grid.GridTransferMessage;
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GridRecipeTransferHandler implements IRecipeTransferHandler<GridContainer> {
    public static final long TRANSFER_SCROLLBAR_DELAY_MS = 200;
    public static long LAST_TRANSFER_TIME;
    IRecipeTransferHandlerHelper transferHelper;

    public GridRecipeTransferHandler(IRecipeTransferHandlerHelper transferHelper) {
        this.transferHelper = transferHelper;
    }

    @Override
    public Class<GridContainer> getContainerClass() {
        return GridContainer.class;
    }

    @Override
    public IRecipeTransferError transferRecipe(@Nonnull GridContainer container, Object recipe, @Nonnull IRecipeLayout recipeLayout, @Nonnull PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
        IGrid grid = container.getGrid();

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
        IScreenInfoProvider gridScreen = container.getScreenInfoProvider();
        if (gridScreen instanceof GridScreen) {
            // Using IGridView#getStacks will return a *filtered* list of items in the view,
            // which will cause problems - especially if the user uses JEI synchronised searching.
            // Instead, we will use IGridView#getAllStacks which provides an unordered view of all GridStacks.
            Collection<IGridStack> gridStacks = ((GridScreen) gridScreen).getView().getAllStacks();

            //Initialise ingredients
            JEIIngredientTracker tracker = new JEIIngredientTracker();
            tracker.init(recipeLayout);

            // Check grid
            for (IGridStack gridStack : gridStacks) {
                tracker.checkIfStackIsNeeded(gridStack.getIngredient(), gridStack.isCraftable());
            }

            // Check inventory
            for (int inventorySlot = 0; inventorySlot < player.inventory.getSizeInventory(); inventorySlot++) {
                if (!player.inventory.getStackInSlot(inventorySlot).isEmpty()) {
                    tracker.checkIfStackIsNeeded(player.inventory.getStackInSlot(inventorySlot), false);
                }
            }

            // Check grid crafting slots
            if (grid.getGridType().equals(GridType.CRAFTING)) {
                CraftingInventory craftingMatrix = grid.getCraftingMatrix();
                if (craftingMatrix != null) {
                    for (int matrixSlot = 0; matrixSlot < craftingMatrix.getSizeInventory(); matrixSlot++) {
                        if (!craftingMatrix.getStackInSlot(matrixSlot).isEmpty()) {
                            tracker.checkIfStackIsNeeded(craftingMatrix.getStackInSlot(matrixSlot), false);
                        }
                    }
                }
            }

            if (tracker.hasMissing()) {
                return new RecipeTransferGridError(tracker);
            }
        }

        return null;
    }

    private boolean isCraftingRecipe(IRecipeCategory<?> recipeCategory) {
        return recipeCategory.getUid().equals(VanillaRecipeCategoryUid.CRAFTING);
    }

}
