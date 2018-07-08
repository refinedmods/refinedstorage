package com.raoulvdberge.refinedstorage.recipe;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.item.ItemCover;
import com.raoulvdberge.refinedstorage.item.ItemHollowWideCover;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipeHollowWideCover extends RecipeHollowCover {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack previousValidSlot = null;

        for (int i = 0; i < 9; ++i) {
            ItemStack slot = inv.getStackInSlot(i);

            if (i == 4) {
                if (!slot.isEmpty()) {
                    return false;
                }
            } else {
                if (isValid(slot, previousValidSlot)) {
                    previousValidSlot = slot;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack stack = new ItemStack(RSItems.HOLLOW_WIDE_COVER, 8);

        ItemHollowWideCover.setItem(stack, ItemCover.getItem(inv.getStackInSlot(0)));

        return stack;
    }
}
