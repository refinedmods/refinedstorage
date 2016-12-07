package com.raoulvdberge.refinedstorage.gui.grid;

import net.minecraft.item.ItemStack;

public class GridFilteredItem {
    private ItemStack stack;
    private int compare;
    private int mode;
    private boolean modFilter;

    public GridFilteredItem(ItemStack stack, int compare, int mode, boolean modFilter) {
        this.stack = stack;
        this.compare = compare;
        this.mode = mode;
        this.modFilter = modFilter;
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

    public boolean isModFilter() {
        return modFilter;
    }
}
