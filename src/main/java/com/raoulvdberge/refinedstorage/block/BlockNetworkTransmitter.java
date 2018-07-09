package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.tile.TileNetworkTransmitter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNetworkTransmitter extends BlockNode {
    public BlockNetworkTransmitter() {
        super(BlockInfoBuilder.forId("network_transmitter").tileEntity(TileNetworkTransmitter::new).create());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return openNetworkGui(RSGui.NETWORK_TRANSMITTER, player, world, pos, side);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
