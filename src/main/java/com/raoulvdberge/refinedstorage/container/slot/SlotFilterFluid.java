package com.raoulvdberge.refinedstorage.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class SlotFilterFluid extends SlotFilter {
    private boolean server;

    public SlotFilterFluid(boolean server, IItemHandler handler, int id, int x, int y) {
        super(handler, id, x, y);

        this.server = server;
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        return server ? super.getStack() : ItemStack.EMPTY;
    }

    public ItemStack getRealStack() {
        return super.getStack();
    }
}