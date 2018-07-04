package com.raoulvdberge.refinedstorage.recipe;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.item.ItemCover;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeCover extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean hadCuttingTool = false;
        boolean hadItem = false;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);

            if (!slot.isEmpty() && hadCuttingTool && hadItem) {
                return false;
            }

            if (slot.getItem() == RSItems.CUTTING_TOOL) {
                if (hadCuttingTool) {
                    return false;
                }

                hadCuttingTool = true;
            }

            if (CoverManager.isValidCover(slot)) {
                if (hadItem) {
                    return false;
                }

                hadItem = true;
            }
        }

        return hadCuttingTool && hadItem;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);

            if (CoverManager.isValidCover(slot)) {
                ItemStack cover = new ItemStack(RSItems.COVER, 6);

                ItemCover.setItem(cover, ItemHandlerHelper.copyStackWithSize(slot, 1));

                return cover;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
}
