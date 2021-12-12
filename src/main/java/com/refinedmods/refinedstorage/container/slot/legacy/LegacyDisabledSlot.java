package com.refinedmods.refinedstorage.container.slot.legacy;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class LegacyDisabledSlot extends LegacyBaseSlot {
    public LegacyDisabledSlot(Container inventory, int inventoryIndex, int x, int y) {
        super(inventory, inventoryIndex, x, y);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }
}
