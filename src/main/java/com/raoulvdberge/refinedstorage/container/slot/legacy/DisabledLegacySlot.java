package com.raoulvdberge.refinedstorage.container.slot.legacy;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class DisabledLegacySlot extends BaseLegacySlot {
    public DisabledLegacySlot(IInventory inventory, int inventoryIndex, int x, int y) {
        super(inventory, inventoryIndex, x, y);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
    }
}
