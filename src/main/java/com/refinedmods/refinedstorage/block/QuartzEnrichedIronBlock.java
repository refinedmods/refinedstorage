package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.util.BlockUtils;

public class QuartzEnrichedIronBlock extends BaseBlock {
    public QuartzEnrichedIronBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(RS.ID, "quartz_enriched_iron_block");
    }
}
