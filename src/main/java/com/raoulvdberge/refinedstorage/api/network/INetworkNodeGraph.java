package com.raoulvdberge.refinedstorage.api.network;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Represents a graph of all the nodes connected to a network.
 */
public interface INetworkNodeGraph {
    /**
     * Rebuilds the network graph.
     */
    void rebuild();

    /**
     * Adds a runnable that is run after the graph is rebuilt.
     *
     * @param action the action to run
     */
    void schedulePostRebuildAction(Consumer<INetwork> action);

    /**
     * @return a collection of all connected nodes
     */
    Collection<INetworkNode> all();

    /**
     * Disconnects and notifies all connected nodes.
     */
    void disconnectAll();
}
