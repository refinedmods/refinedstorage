package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeNetworkReceiver;

import javax.annotation.Nonnull;

public class TileNetworkReceiver extends TileNode<NetworkNodeNetworkReceiver> {
    @Override
    @Nonnull
    public NetworkNodeNetworkReceiver createNode() {
        return new NetworkNodeNetworkReceiver(this);
    }
}
