package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.render.PropertyObject;
import com.raoulvdberge.refinedstorage.tile.TileDiskManipulator;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;

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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(RS.INSTANCE, RSGui.DISK_MANIPULATOR, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    protected BlockStateContainer.Builder createBlockStateBuilder() {
        return super.createBlockStateBuilder().add(DISK_STATE);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(DISK_STATE, ((TileDiskManipulator) world.getTileEntity(pos)).getDiskState());
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        ((TileDiskManipulator) world.getTileEntity(pos)).onBreak();

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public EnumPlacementType getPlacementType() {
        return EnumPlacementType.HORIZONTAL;
    }
}
