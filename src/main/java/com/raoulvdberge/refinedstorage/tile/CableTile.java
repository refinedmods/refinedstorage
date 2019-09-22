package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CableTile extends NetworkNodeTile<NetworkNodeCable> {
    public CableTile() {
        super(RSTiles.CABLE);
    }

    @Override
    @Nonnull
    public NetworkNodeCable createNode(World world, BlockPos pos) {
        return new NetworkNodeCable(world, pos);
    }
}
