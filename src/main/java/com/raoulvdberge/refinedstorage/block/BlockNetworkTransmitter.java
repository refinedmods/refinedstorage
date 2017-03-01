package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.tile.TileNetworkTransmitter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNetworkTransmitter extends BlockNode {
    public BlockNetworkTransmitter() {
        super("network_transmitter");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            tryOpenNetworkGui(RSGui.NETWORK_TRANSMITTER, player, world, pos, side);
        }

        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileNetworkTransmitter();
    }

    @Override
    public PlacementType getPlacementType() {
        return null;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
