package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeRelay;

public class TileRelay extends TileNode {
    @Override
    public INetworkNode createNode() {
        return new NetworkNodeRelay(this);
    }
}
