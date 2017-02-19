package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCable;

import javax.annotation.Nonnull;

public class TileCable extends TileNode<NetworkNodeCable> {
    @Override
    @Nonnull
    public NetworkNodeCable createNode() {
        return new NetworkNodeCable(this);
    }
}
