package com.raoulvdberge.refinedstorage.api.network;

import java.util.List;

/**
 * Represents a graph of all the nodes connected to a network.
 */
public interface INetworkNodeGraph {
    /**
    /**
     * Rebuilds the network graph.
     */
    void rebuild();

    /**
     * @return a list of all connected nodes
     */
    List<INetworkNode> all();

    /**
     * Replaces an old node with a new one.
     *
     * @param node the node to replace
     */
    void replace(INetworkNode node);

    /**
     * Disconnects and notifies all connected nodes.
     */
    void disconnectAll();
}
