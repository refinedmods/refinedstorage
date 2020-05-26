package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.CableNetworkNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CableTile extends NetworkNodeTile<CableNetworkNode> {
    public CableTile() {
        super(RSTiles.CABLE);
    }

    @Override
    @Nonnull
    public CableNetworkNode createNode(World world, BlockPos pos) {
        return new CableNetworkNode(world, pos);
    }
}
