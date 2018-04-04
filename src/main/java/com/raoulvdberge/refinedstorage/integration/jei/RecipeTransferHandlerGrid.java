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
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecipeTransferHandlerGrid implements IRecipeTransferHandler {
    @Override
    public Class<? extends Container> getContainerClass() {
        return ContainerGrid.class;
    }

    @Override
    public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            IGrid grid = ((ContainerGrid) container).getGrid();

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
                Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs = recipeLayout.getItemStacks().getGuiIngredients();

                NBTTagCompound recipe = new NBTTagCompound();

                for (Slot slot : container.inventorySlots) {
                    if (slot.inventory instanceof InventoryCrafting) {
                        IGuiIngredient<ItemStack> ingredient = inputs.get(slot.getSlotIndex() + 1);

                        if (ingredient != null) {
                            NBTTagList tags = new NBTTagList();

                            for (ItemStack possibleItem : ingredient.getAllIngredients()) {
                                if (possibleItem != null) {
                                    possibleItem = possibleItem.copy();
                                    possibleItem.setTagCompound(possibleItem.getItem().getNBTShareTag(possibleItem));

                                    NBTTagCompound tag = new NBTTagCompound();
                                    possibleItem.writeToNBT(tag);

                                    tags.appendTag(tag);
                                }
                            }

                            recipe.setTag("#" + slot.getSlotIndex(), tags);
                        }
                    }
                }

                RS.INSTANCE.network.sendToServer(new MessageGridTransfer(recipe));
            }
        }

        return null;
    }
}
