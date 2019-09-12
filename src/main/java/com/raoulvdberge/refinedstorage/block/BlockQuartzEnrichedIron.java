package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;

public class BlockQuartzEnrichedIron extends BlockBase {
    public BlockQuartzEnrichedIron() {
        super(BlockInfoBuilder.forId("quartz_enriched_iron_block").create());
    }

    /* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    }*/
}
