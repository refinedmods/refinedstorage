package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewRequestMessage;
import com.refinedmods.refinedstorage.network.grid.GridProcessingTransferMessage;
import com.refinedmods.refinedstorage.network.grid.GridTransferMessage;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.client.gui.screen.Screen;
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

    @Override
    public Class<GridContainer> getContainerClass() {
        return GridContainer.class;
    }

    @Override
    public IRecipeTransferError transferRecipe(@Nonnull GridContainer container, Object recipe, @Nonnull IRecipeLayout recipeLayout, @Nonnull PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
        IngredientTracker tracker = trackItems(container, recipeLayout, player);

        if (tracker.hasMissing()) {
            if (doTransfer && Screen.hasControlDown()) {
                tracker.getCraftingRequests().forEach((id, count) -> {
                    RS.NETWORK_HANDLER.sendToServer(new GridCraftingPreviewRequestMessage(id, count, Screen.hasShiftDown(), false));
                });
            } else if (doTransfer) {
                moveItems(container, recipeLayout);
            } else {
                return new RecipeTransferGridError(tracker);
            }
        }

        return null;
    }

    private IngredientTracker trackItems(GridContainer container, IRecipeLayout recipeLayout, PlayerEntity player) {
        IngredientTracker tracker = new IngredientTracker(recipeLayout);
        if (!(container.getScreenInfoProvider() instanceof GridScreen)) {
            return tracker;
        }

        // Using IGridView#getStacks will return a *filtered* list of items in the view,
        // which will cause problems - especially if the user uses JEI synchronised searching.
        // Instead, we will use IGridView#getAllStacks which provides an unordered view of all GridStacks.
        Collection<IGridStack> gridStacks = ((GridScreen) container.getScreenInfoProvider()).getView().getAllStacks();

        // Check grid
        for (IGridStack gridStack : gridStacks) {
            if (gridStack instanceof ItemGridStack) {
                tracker.addAvailableStack(((ItemGridStack) gridStack).getStack(), gridStack);
            }
        }

        // Check inventory
        for (int inventorySlot = 0; inventorySlot < player.inventory.getSizeInventory(); inventorySlot++) {
            if (!player.inventory.getStackInSlot(inventorySlot).isEmpty()) {
                tracker.addAvailableStack(player.inventory.getStackInSlot(inventorySlot), null);
            }
        }

        // Check grid crafting slots
        if (container.getGrid().getGridType().equals(GridType.CRAFTING)) {
            CraftingInventory craftingMatrix = container.getGrid().getCraftingMatrix();
            if (craftingMatrix != null) {
                for (int matrixSlot = 0; matrixSlot < craftingMatrix.getSizeInventory(); matrixSlot++) {
                    if (!craftingMatrix.getStackInSlot(matrixSlot).isEmpty()) {
                        tracker.addAvailableStack(craftingMatrix.getStackInSlot(matrixSlot), null);
                    }
                }
            }
        }

        return tracker;
    }

    private void moveItems(GridContainer gridContainer, IRecipeLayout recipeLayout) {
        IGrid grid = gridContainer.getGrid();

        LAST_TRANSFER_TIME = System.currentTimeMillis();

        if (grid.getGridType() == GridType.PATTERN && !recipeLayout.getRecipeCategory().getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
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
                gridContainer.inventorySlots.stream().filter(s -> s.inventory instanceof CraftingInventory).collect(Collectors.toList())
            ));
        }
    }

}

