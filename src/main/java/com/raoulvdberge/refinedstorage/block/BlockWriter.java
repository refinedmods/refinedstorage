package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IWriter;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeWriter;
import com.raoulvdberge.refinedstorage.tile.TileWriter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (hitCablePart(state, world, pos, hitX, hitY, hitZ)) {
            return false;
        }

        if (!world.isRemote) {
            NetworkNodeWriter writer = ((TileWriter) world.getTileEntity(pos)).getNode();

            if (player.isSneaking()) {
                if (writer.getNetwork() != null) {
                    IReaderWriterChannel channel = writer.getNetwork().getReaderWriterChannel(writer.getChannel());

                    if (channel != null) {
                        channel.getHandlers().stream().map(h -> h.getStatusWriter(writer, channel)).flatMap(List::stream).forEach(player::sendMessage);
                    }
                }
            } else if (tryOpenNetworkGui(RSGui.READER_WRITER, player, world, pos, side)) {
                writer.onOpened(player);
            }
        }

        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileWriter();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IWriter writer = ((TileWriter) world.getTileEntity(pos)).getNode();

        return side == writer.getDirection().getOpposite() ? writer.getRedstoneStrength() : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getWeakPower(state, world, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);

        return tile instanceof TileWriter && side == ((TileWriter) tile).getDirection().getOpposite();
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
