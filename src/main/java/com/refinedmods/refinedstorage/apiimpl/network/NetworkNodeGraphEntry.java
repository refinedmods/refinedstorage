package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphEntry;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;

public class NetworkNodeGraphEntry implements INetworkNodeGraphEntry {
    private final INetworkNode node;

    public NetworkNodeGraphEntry(INetworkNode node) {
        this.node = node;
    }

    @Override
    public INetworkNode getNode() {
        return node;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof NetworkNodeGraphEntry)) {
            return false;
        }
        if (this == other) {
            return true;
        }

        NetworkNodeGraphEntry otherItem = (NetworkNodeGraphEntry) other;

        if (node.getLevel().dimension() != otherItem.node.getLevel().dimension()) {
            return false;
        }

        return node.getPos().equals(otherItem.node.getPos());
    }

    @Override
    public int hashCode() {
        int result = node.getPos().hashCode();
        result = 31 * result + node.getLevel().dimension().hashCode();
        return result;
    }
}
