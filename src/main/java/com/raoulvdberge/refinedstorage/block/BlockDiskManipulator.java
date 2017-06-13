package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.tile.TileDiskManipulator;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

public class BlockDiskManipulator extends BlockNode {
    public static final PropertyObject<Integer[]> DISK_STATE = new PropertyObject<>("disk_state", Integer[].class);

    public BlockDiskManipulator() {
        super("disk_manipulator");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDiskManipulator();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.DISK_MANIPULATOR, player, world, pos, side);
        }

        return true;
    }

    @Override
    protected BlockStateContainer.Builder createBlockStateBuilder() {
        return super.createBlockStateBuilder().add(DISK_STATE);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState s = super.getExtendedState(state, world, pos);

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileDiskManipulator) {
            s = ((IExtendedBlockState) s).withProperty(DISK_STATE, ((TileDiskManipulator) tile).getDiskState());
        }

        return s;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        ((TileDiskManipulator) world.getTileEntity(pos)).getNode().onBreak();

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
