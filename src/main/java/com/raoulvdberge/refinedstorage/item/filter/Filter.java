package com.raoulvdberge.refinedstorage.item.filter;

import net.minecraft.item.ItemStack;

public class Filter {
    private ItemStack stack;
    private int compare;
    private int mode;
    private boolean modFilter;

    public Filter(ItemStack stack, int compare, int mode, boolean modFilter) {
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
