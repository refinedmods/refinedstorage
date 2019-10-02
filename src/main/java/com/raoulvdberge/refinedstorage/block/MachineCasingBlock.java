package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.util.BlockUtils;

public class MachineCasingBlock extends BaseBlock {
    public MachineCasingBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(RS.ID, "machine_casing");
    }
}
