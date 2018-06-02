package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSBlocks;

public class ItemBlockStorage extends ItemBlockBase {
    public ItemBlockStorage() {
        super(RSBlocks.STORAGE, RSBlocks.STORAGE.getDirection(), true);
    }
}
