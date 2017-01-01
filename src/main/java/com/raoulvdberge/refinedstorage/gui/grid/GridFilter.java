package com.raoulvdberge.refinedstorage.gui.grid;

import net.minecraft.item.ItemStack;

public class GridFilter {
    private ItemStack stack;
    private int compare;

    public GridFilter(ItemStack stack, int compare) {
        this.stack = stack;
        this.compare = compare;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getCompare() {
        return compare;
    }
}
