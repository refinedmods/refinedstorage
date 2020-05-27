package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.RelayNetworkNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RelayTile extends NetworkNodeTile<RelayNetworkNode> {
    public RelayTile() {
        super(RSTiles.RELAY);
    }

    @Override
    @Nonnull
    public RelayNetworkNode createNode(World world, BlockPos pos) {
        return new RelayNetworkNode(world, pos);
    }
}
