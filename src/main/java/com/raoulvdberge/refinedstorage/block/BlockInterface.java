package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileInterface;

public class BlockInterface extends BlockNode {
    public BlockInterface() {
        super(BlockInfoBuilder.forId("interface").tileEntity(TileInterface::new).create());
    }

    /*
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.INTERFACE, player, world, pos, side, Permission.MODIFY, Permission.INSERT, Permission.EXTRACT);
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
