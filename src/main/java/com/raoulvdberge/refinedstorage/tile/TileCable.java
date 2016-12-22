package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCable;

public class TileCable extends TileNode {
    @Override
    public INetworkNode createNode() {
        return new NetworkNodeCable();
    }
}
