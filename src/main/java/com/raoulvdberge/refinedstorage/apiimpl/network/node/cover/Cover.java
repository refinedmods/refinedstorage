package com.raoulvdberge.refinedstorage.apiimpl.network.node.cover;

import net.minecraft.item.ItemStack;

public class Cover {
    private ItemStack stack;
    private boolean hollow;

    public Cover(ItemStack stack, boolean hollow) {
        this.stack = stack;
        this.hollow = hollow;
    }

    public ItemStack getStack() {
        return stack;
    }

    public boolean isHollow() {
        return hollow;
    }
}
