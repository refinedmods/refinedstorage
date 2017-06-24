package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileCable extends TileNode<NetworkNodeCable> {
    @Override
    @Nonnull
    public NetworkNodeCable createNode(World world, BlockPos pos) {
        return new NetworkNodeCable(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeCable.ID;
    }
}
