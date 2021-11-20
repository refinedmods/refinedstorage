package com.refinedmods.refinedstorage.api.network;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;

/**
 * An entry in the network graph.
 * Implementors MUST implement equals and hashCode.
 */
public interface INetworkNodeGraphEntry {
    INetworkNode getNode();
}
