package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;

public class QuartzEnrichedIronBlock extends Block {
    public QuartzEnrichedIronBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(RS.ID, "quartz_enriched_iron_block");
    }
}
