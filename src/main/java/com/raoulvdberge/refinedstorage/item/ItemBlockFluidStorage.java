package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSBlocks;

public class ItemBlockFluidStorage extends ItemBlockBase {
    public ItemBlockFluidStorage() {
        super(RSBlocks.FLUID_STORAGE, RSBlocks.FLUID_STORAGE.getDirection(), true);
    }
}
