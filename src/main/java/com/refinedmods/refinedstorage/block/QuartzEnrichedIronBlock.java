package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.util.ResourceLocation;

public class QuartzEnrichedIronBlock extends BaseBlock {
    public QuartzEnrichedIronBlock(ResourceLocation registryName) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(registryName);
    }
}
