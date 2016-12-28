package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeRelay;

import javax.annotation.Nonnull;

public class TileRelay extends TileNode<NetworkNodeRelay> {
    @Override
    @Nonnull
    public NetworkNodeRelay createNode() {
        return new NetworkNodeRelay(this);
    }
}
