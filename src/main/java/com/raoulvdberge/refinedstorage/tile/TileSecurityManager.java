package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeSecurityManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileSecurityManager extends TileNode<NetworkNodeSecurityManager> {
    public TileSecurityManager() {
        super(RSTiles.SECURITY_MANAGER);
    }

    @Override
    @Nonnull
    public NetworkNodeSecurityManager createNode(World world, BlockPos pos) {
        return new NetworkNodeSecurityManager(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeSecurityManager.ID;
    }
}
