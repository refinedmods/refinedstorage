package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileDiskManipulator;

public class BlockDiskManipulator extends BlockNode {
    // TODO public static final PropertyObject<Integer[]> DISK_STATE = new PropertyObject<>("disk_state", Integer[].class);

    public BlockDiskManipulator() {
        super(BlockInfoBuilder.forId("disk_manipulator").tileEntity(TileDiskManipulator::new).create());
    }

    /* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));

        modelRegistration.addModelLoader(() -> new CustomModelLoaderDefault(info.getId(), ModelDiskManipulator::new));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.DISK_MANIPULATOR, player, world, pos, side);
    }

    @Override
    protected BlockStateContainer.Builder createBlockStateBuilder() {
        return super.createBlockStateBuilder().add(DISK_STATE);
    }

    @Override
    public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos) {
        BlockState s = super.getExtendedState(state, world, pos);

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileDiskManipulator) {
            s = ((IExtendedBlockState) s).withProperty(DISK_STATE, ((TileDiskManipulator) tile).getDiskState());
        }

        return s;
    }*/

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
