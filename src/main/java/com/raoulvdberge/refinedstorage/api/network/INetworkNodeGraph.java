package com.raoulvdberge.refinedstorage.api.network;

import java.util.List;

/**
 * Represents a graph of all the nodes connected to a network.
 */
public interface INetworkNodeGraph {
    /**
     * Rebuilds the network graph.
     */
    void rebuild();

    /**
     * @return a list of all connected nodes
     */
    List<INetworkNode> all();

    /**
     * Disconnects and notifies all connected nodes.
     */
    void disconnectAll();
}
