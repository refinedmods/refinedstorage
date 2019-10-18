package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkReceiverNetworkNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class NetworkReceiverTile extends NetworkNodeTile<NetworkReceiverNetworkNode> {
    public NetworkReceiverTile() {
        super(RSTiles.NETWORK_RECEIVER);
    }

    @Override
    @Nonnull
    public NetworkReceiverNetworkNode createNode(World world, BlockPos pos) {
        return new NetworkReceiverNetworkNode(world, pos);
    }
}
