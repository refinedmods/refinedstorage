package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeSecurityManager;

import javax.annotation.Nonnull;

public class TileSecurityManager extends TileNode<NetworkNodeSecurityManager> {
    @Override
    @Nonnull
    public NetworkNodeSecurityManager createNode() {
        return new NetworkNodeSecurityManager(this);
    }
}
