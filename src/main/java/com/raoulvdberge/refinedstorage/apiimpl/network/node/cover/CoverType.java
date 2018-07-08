package com.raoulvdberge.refinedstorage.apiimpl.network.node.cover;

import com.raoulvdberge.refinedstorage.RSItems;
import net.minecraft.item.ItemStack;

public enum CoverType {
    NORMAL,
    HOLLOW,
    HOLLOW_WIDE;

    public boolean isHollow() {
        return this == HOLLOW || this == HOLLOW_WIDE;
    }

    public ItemStack createStack() {
        return new ItemStack(this == NORMAL ? RSItems.COVER : (this == HOLLOW ? RSItems.HOLLOW_COVER : RSItems.HOLLOW_WIDE_COVER));
    }
}
