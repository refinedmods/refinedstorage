package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.TileNode;

public class TileCraftingMonitor extends TileNode {
    @Override
    public INetworkNode createNode() {
        return new NetworkNodeCraftingMonitor(this);
    }
}
