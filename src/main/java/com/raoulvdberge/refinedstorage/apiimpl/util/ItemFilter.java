package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.raoulvdberge.refinedstorage.api.util.IFilter;
import net.minecraft.item.ItemStack;

public class ItemFilter implements IFilter<ItemStack> {
    private ItemStack stack;
    private int compare;
    private int mode;
    private boolean modFilter;

    public ItemFilter(ItemStack stack, int compare, int mode, boolean modFilter) {
        this.stack = stack;
        this.compare = compare;
        this.mode = mode;
        this.modFilter = modFilter;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public boolean isModFilter() {
        return modFilter;
    }
}
