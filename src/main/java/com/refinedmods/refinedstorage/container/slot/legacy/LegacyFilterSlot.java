package com.refinedmods.refinedstorage.container.slot.legacy;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class LegacyFilterSlot extends LegacyBaseSlot {
    public LegacyFilterSlot(IInventory inventory, int inventoryIndex, int x, int y) {
        super(inventory, inventoryIndex, x, y);
    }

    @Override
    public boolean mayPickup(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return true;
    }

    @Override
    public void set(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.setCount(1);
        }

        super.set(stack);
    }
}
