package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.SecurityManagerNetworkNode;
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
