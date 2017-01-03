package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.TileNode;

import javax.annotation.Nonnull;

public class TileCraftingMonitor extends TileNode<NetworkNodeCraftingMonitor> {
    @Override
    @Nonnull
    public NetworkNodeCraftingMonitor createNode() {
        return new NetworkNodeCraftingMonitor(this);
    }
}
