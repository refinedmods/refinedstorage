package com.raoulvdberge.refinedstorage.inventory.listener;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;

public class NetworkNodeInventoryListener implements InventoryListener<BaseItemHandler> {
    private INetworkNode node;

    public NetworkNodeInventoryListener(INetworkNode node) {
        this.node = node;
    }

    @Override
    public void onChanged(BaseItemHandler handler, int slot, boolean reading) {
        if (!reading) {
            node.markDirty();
        }
    }
}
