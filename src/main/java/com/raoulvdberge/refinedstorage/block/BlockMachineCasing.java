package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;

public class BlockMachineCasing extends BlockBase {
    public BlockMachineCasing() {
        super(BlockInfoBuilder.forId("machine_casing").create());
    }
/* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    } */
}
