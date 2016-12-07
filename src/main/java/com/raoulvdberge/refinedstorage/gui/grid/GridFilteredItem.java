package com.raoulvdberge.refinedstorage.gui.grid;

import net.minecraft.item.ItemStack;

public class GridFilteredItem {
    private ItemStack stack;
    private int compare;
    private int mode;

    public GridFilteredItem(ItemStack stack, int compare, int mode) {
        this.stack = stack;
        this.compare = compare;
        this.mode = mode;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getCompare() {
        return compare;
    }

    public int getMode() {
        return mode;
    }
}
