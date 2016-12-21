package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeSecurityManager;

public class TileSecurityManager extends TileNode {
    @Override
    public INetworkNode createNode() {
        return new NetworkNodeSecurityManager(this);
    }
}
