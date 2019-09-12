package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileNetworkReceiver;

public class BlockNetworkReceiver extends BlockNode {
    public BlockNetworkReceiver() {
        super(BlockInfoBuilder.forId("network_receiver").tileEntity(TileNetworkReceiver::new).create());
    }
/* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(base, RS.ID + ":blocks/network_receiver/cutouts/connected"));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
