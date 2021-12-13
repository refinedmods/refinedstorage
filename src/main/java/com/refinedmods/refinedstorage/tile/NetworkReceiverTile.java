package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkReceiverNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class NetworkReceiverTile extends NetworkNodeTile<NetworkReceiverNetworkNode> {
    public NetworkReceiverTile(BlockPos pos, BlockState state) {
        super(RSTiles.NETWORK_RECEIVER, pos, state);
    }

    @Override
    @Nonnull
    public NetworkReceiverNetworkNode createNode(Level level, BlockPos pos) {
        return new NetworkReceiverNetworkNode(level, pos);
    }
}
