package com.raoulvdberge.refinedstorage.apiimpl.network.node.cover;

import com.raoulvdberge.refinedstorage.RSItems;
import net.minecraft.item.ItemStack;

public enum CoverType {
    NORMAL,
    HOLLOW,
    HOLLOW_MEDIUM,
    HOLLOW_LARGE;

    public boolean isHollow() {
        return this == HOLLOW || this == HOLLOW_MEDIUM || this == HOLLOW_LARGE;
    }

    public ItemStack createStack() {
        return new ItemStack(this == NORMAL ? RSItems.COVER : RSItems.HOLLOW_COVER);
    }
}
