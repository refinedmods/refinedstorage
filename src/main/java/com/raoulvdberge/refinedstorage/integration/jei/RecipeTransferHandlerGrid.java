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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeTransferHandlerGrid implements IRecipeTransferHandler {
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

    @Override
    public Class<? extends Container> getContainerClass() {
        return ContainerGrid.class;
    }

    @Override
    public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        IGrid grid = ((ContainerGrid) container).getGrid();

        if (doTransfer) {
            LAST_TRANSFER = System.currentTimeMillis();

            if (grid.getType() == GridType.PATTERN && ((NetworkNodeGrid) grid).isProcessingPattern()) {
                List<ItemStack> inputs = new LinkedList<>();
                List<ItemStack> outputs = new LinkedList<>();

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

                RS.INSTANCE.network.sendToServer(new MessageGridProcessingTransfer(inputs, outputs));
            } else {
                RS.INSTANCE.network.sendToServer(new MessageGridTransfer(recipeLayout.getItemStacks().getGuiIngredients(), container.inventorySlots.stream().filter(s -> s.inventory instanceof InventoryCrafting).collect(Collectors.toList())));
            }
        } else {
            if (grid.getType() == GridType.PATTERN && ((NetworkNodeGrid) grid).isProcessingPattern()) {
                if (recipeLayout.getRecipeCategory().getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
                    return ERROR_CANNOT_TRANSFER;
                }
            } else {
                if (!recipeLayout.getRecipeCategory().getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
                    return ERROR_CANNOT_TRANSFER;
                }
            }
        }

        return null;
    }
}
