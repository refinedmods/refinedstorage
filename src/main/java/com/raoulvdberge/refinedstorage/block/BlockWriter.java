package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.tile.TileWriter;
import mcmultipart.microblock.MicroblockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockWriter extends BlockCable {
    public BlockWriter() {
        super("writer");
    }

    @Override
    public List<AxisAlignedBB> getNonUnionizedCollisionBoxes(IBlockState state) {
        return RSBlocks.CONSTRUCTOR.getNonUnionizedCollisionBoxes(state);
    }

    @Override
    public boolean onBlockActivatedDefault(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hitCablePart(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            player.openGui(RS.INSTANCE, RSGui.READER_WRITER, world, pos.getX(), pos.getY(), pos.getZ());

            ((TileWriter) world.getTileEntity(pos)).onOpened(player);
        }

        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileWriter();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPowerDefault(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side, MicroblockContainer partContainer) {
        TileWriter writer = (TileWriter) world.getTileEntity(pos);

        return side == writer.getDirection().getOpposite() ? writer.getRedstoneStrength() : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPowerDefault(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side, MicroblockContainer partContainer) {
        return getWeakPowerDefault(state, world, pos, side, partContainer);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstoneDefault(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side, MicroblockContainer partContainer) {
        return side == ((TileWriter) world.getTileEntity(pos)).getDirection().getOpposite();
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public EnumPlacementType getPlacementType() {
        return EnumPlacementType.ANY_FACE_PLAYER;
    }
}
