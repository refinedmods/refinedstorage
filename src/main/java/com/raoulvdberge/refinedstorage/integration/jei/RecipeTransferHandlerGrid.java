package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.network.MessageGridProcessingTransfer;
import com.raoulvdberge.refinedstorage.network.MessageGridTransfer;
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
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedList;
import java.util.List;
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
