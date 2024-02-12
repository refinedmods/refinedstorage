package com.refinedmods.refinedstorage.container.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import javax.annotation.Nonnull;

public class OutputSlot extends BaseSlot {
    public OutputSlot(IItemHandler inventory, int inventoryIndex, int x, int y) {
        super(inventory, inventoryIndex, x, y);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }
}
