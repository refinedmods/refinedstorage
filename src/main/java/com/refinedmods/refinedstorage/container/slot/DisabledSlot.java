package com.refinedmods.refinedstorage.container.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import javax.annotation.Nonnull;

public class DisabledSlot extends BaseSlot {
    public DisabledSlot(IItemHandler itemHandler, int inventoryIndex, int x, int y) {
        super(itemHandler, inventoryIndex, x, y);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }
}
