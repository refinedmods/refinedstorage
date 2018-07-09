package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;

public class BlockMachineCasing extends BlockBase {
    public BlockMachineCasing() {
        super(BlockInfoBuilder.forId("machine_casing").create());
    }
}
