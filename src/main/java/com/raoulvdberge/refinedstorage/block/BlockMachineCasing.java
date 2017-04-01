package com.raoulvdberge.refinedstorage.block;

import javax.annotation.Nullable;

public class BlockMachineCasing extends BlockBase {
    public BlockMachineCasing() {
        super("machine_casing");
    }

    @Override
    @Nullable
    public Direction getDirection() {
        return null;
    }
}
