package com.raoulvdberge.refinedstorage.inventory.listener;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;

import java.util.function.Consumer;

public class NetworkNodeListener implements Consumer<Integer> {
    private INetworkNode node;

    public NetworkNodeListener(INetworkNode node) {
        this.node = node;
    }

    @Override
    public void accept(Integer slot) {
        node.markDirty();
    }
}
