package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeBasic;

public class TileCable extends TileNode {
    @Override
    public INetworkNode createNode() {
        return new NetworkNodeBasic(this, RS.INSTANCE.config.cableUsage, false);
    }
}
