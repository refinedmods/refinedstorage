package com.raoulvdberge.refinedstorage.api.network;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;

import java.util.Collection;

/**
 * Represents a graph of all the nodes connected to a network.
 */
public interface INetworkNodeGraph {
    /**
     * Rebuilds the network graph.
     */
    void rebuild();

    /**
     * @return a collection of all connected nodes
     */
    Collection<INetworkNode> all();

    /**
     * Disconnects and notifies all connected nodes.
     */
    void disconnectAll();
}
