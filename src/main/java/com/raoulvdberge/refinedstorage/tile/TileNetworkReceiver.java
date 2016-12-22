package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeNetworkReceiver;

public class TileNetworkReceiver extends TileNode {
    @Override
    public INetworkNode createNode() {
        return new NetworkNodeNetworkReceiver(this);
    }
}
