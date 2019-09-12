package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileFluidInterface;

public class BlockFluidInterface extends BlockNode {
    public BlockFluidInterface() {
        super(BlockInfoBuilder.forId("fluid_interface").tileEntity(TileFluidInterface::new).create());
    }
/* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.FLUID_INTERFACE, player, world, pos, side, Permission.MODIFY, Permission.INSERT, Permission.EXTRACT);
    } */

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
