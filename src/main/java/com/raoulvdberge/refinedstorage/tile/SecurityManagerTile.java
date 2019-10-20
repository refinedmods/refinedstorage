package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.SecurityManagerNetworkNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SecurityManagerTile extends NetworkNodeTile<SecurityManagerNetworkNode> {
    public SecurityManagerTile() {
        super(RSTiles.SECURITY_MANAGER);
    }

    @Override
    @Nonnull
    public SecurityManagerNetworkNode createNode(World world, BlockPos pos) {
        return new SecurityManagerNetworkNode(world, pos);
    }
}
