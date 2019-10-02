package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.tile.DiskDriveTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class DiskDriveBlock extends NodeBlock {
    // TODO public static final PropertyObject<Integer[]> DISK_STATE = new PropertyObject<>("disk_state", Integer[].class);

    public DiskDriveBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.setRegistryName(RS.ID, "disk_drive");
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new DiskDriveTile();
    }

/* TODO
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));

        modelRegistration.addModelLoader(() -> new CustomModelLoaderDefault(info.getId(), ModelDiskDrive::new));
    }

    @Override
    @Nullable
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.DISK_DRIVE, player, world, pos, side);
    }

    @Override
    protected BlockStateContainer.Builder createBlockStateBuilder() {
        return super.createBlockStateBuilder().add(DISK_STATE);
    }

    @Override
    public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos) {
        BlockState s = super.getExtendedState(state, world, pos);

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileDiskDrive) {
            s = ((IExtendedBlockState) s).withProperty(DISK_STATE, ((TileDiskDrive) tile).getDiskState());
        }

        return s;
    }*/
}
