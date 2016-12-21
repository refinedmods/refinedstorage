package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;

public class ItemHandlerListenerNetworkNode implements IItemHandlerListener {
    private INetworkNode node;

    public ItemHandlerListenerNetworkNode(INetworkNode node) {
        this.node = node;
    }

    @Override
    public void onChanged(int slot) {
        node.markDirty();
    }
}
