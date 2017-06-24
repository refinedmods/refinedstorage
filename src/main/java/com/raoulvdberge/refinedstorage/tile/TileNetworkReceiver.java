package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeNetworkReceiver;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileNetworkReceiver extends TileNode<NetworkNodeNetworkReceiver> {
    @Override
    @Nonnull
    public NetworkNodeNetworkReceiver createNode(World world, BlockPos pos) {
        return new NetworkNodeNetworkReceiver(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeNetworkReceiver.ID;
    }
}
