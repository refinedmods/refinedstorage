package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.util.BlockUtils;

public class MachineCasingBlock extends BaseBlock {
    public MachineCasingBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(RS.ID, "machine_casing");
    }
}
