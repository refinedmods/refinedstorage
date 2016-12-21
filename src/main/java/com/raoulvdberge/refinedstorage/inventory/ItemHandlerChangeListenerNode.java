package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;

public class ItemHandlerChangeListenerNode implements IItemHandlerChangeListener {
    private INetworkNode node;

    public ItemHandlerChangeListenerNode(INetworkNode node) {
        this.node = node;
    }

    @Override
    public void onChanged(int slot) {
        node.markDirty();
    }
}
