package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;

public abstract class BlockNode extends BlockBase {
    public BlockNode(IBlockInfo info) {
        super(info);
    }

    public boolean hasConnectedState() {
        return false;
    }
}
