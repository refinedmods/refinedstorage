package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;

import java.util.function.Consumer;

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
