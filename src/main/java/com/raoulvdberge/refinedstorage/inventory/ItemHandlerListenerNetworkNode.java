package com.raoulvdberge.refinedstorage.inventory;

import java.util.function.Consumer;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;

public class ItemHandlerListenerNetworkNode implements Consumer<Integer> {
    private INetworkNode node;

    public ItemHandlerListenerNetworkNode(INetworkNode node) {
        this.node = node;
    }

    @Override
    public void accept(Integer slot) {
        node.markDirty();
    }
}
