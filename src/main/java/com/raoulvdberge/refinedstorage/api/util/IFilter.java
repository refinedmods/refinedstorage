package com.raoulvdberge.refinedstorage.api.util;

import net.minecraft.item.ItemStack;

public interface IFilter {
    ItemStack getStack();

    int getCompare();

    int getMode();

    boolean isModFilter();
}
