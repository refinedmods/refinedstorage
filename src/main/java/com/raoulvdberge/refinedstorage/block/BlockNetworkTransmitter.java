package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileNetworkTransmitter;

public class BlockNetworkTransmitter extends BlockNode {
    public BlockNetworkTransmitter() {
        super(BlockInfoBuilder.forId("network_transmitter").tileEntity(TileNetworkTransmitter::new).create());
    }

    /* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));

        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelFullbright(base, RS.ID + ":blocks/network_transmitter/cutouts/connected"));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.NETWORK_TRANSMITTER, player, world, pos, side);
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
