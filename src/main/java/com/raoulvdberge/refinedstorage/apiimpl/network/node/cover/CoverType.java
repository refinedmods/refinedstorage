package com.raoulvdberge.refinedstorage.apiimpl.network.node.cover;

import com.raoulvdberge.refinedstorage.RSItems;
import net.minecraft.item.ItemStack;

public enum CoverType {
    NORMAL,
    HOLLOW;

    public ItemStack createStack() {
        return new ItemStack(this == NORMAL ? RSItems.COVER : RSItems.HOLLOW_COVER);
    }
}
