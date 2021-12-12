package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.apiimpl.network.node.cover.Cover;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverType;
import net.minecraft.world.item.ItemStack;

public class HollowCoverItem extends CoverItem {

    public HollowCoverItem() {
        super();
    }

    @Override
    protected Cover createCover(ItemStack stack) {
        return new Cover(stack, CoverType.HOLLOW);
    }
}
