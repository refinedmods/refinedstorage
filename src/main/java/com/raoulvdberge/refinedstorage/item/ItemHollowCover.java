package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.Cover;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverType;
import net.minecraft.item.ItemStack;

public class ItemHollowCover extends ItemCover {
    public ItemHollowCover() {
        super("hollow_cover");
    }

    @Override
    protected Cover createCover(ItemStack stack) {
        return new Cover(stack, CoverType.HOLLOW);
    }
}
