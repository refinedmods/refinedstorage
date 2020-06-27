package com.refinedmods.refinedstorage.inventory.listener;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;

public class NetworkNodeInventoryListener implements InventoryListener<BaseItemHandler> {
    private final INetworkNode node;

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
