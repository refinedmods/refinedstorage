package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.Cover;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverType;
import net.minecraft.item.ItemStack;

public class ItemHollowWideCover extends ItemCover {
    public ItemHollowWideCover() {
        super("hollow_wide_cover");
    }

    @Override
    protected Cover createCover(ItemStack stack) {
        return new Cover(stack, CoverType.HOLLOW_WIDE);
    }
}
