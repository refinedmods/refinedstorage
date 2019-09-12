package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeRelay;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileRelay extends TileNode<NetworkNodeRelay> {
    public TileRelay() {
        super(RSTiles.RELAY);
    }

    @Override
    @Nonnull
    public NetworkNodeRelay createNode(World world, BlockPos pos) {
        return new NetworkNodeRelay(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeRelay.ID;
    }
}
