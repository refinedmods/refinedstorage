package com.raoulvdberge.refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a graph of all the nodes connected to a network.
 */
public interface INetworkNodeGraph {
    /**
     * Rebuilds the node graph.
     *
     * @param start  the starting position to start looking for nodes, or null to start at network begin position
     * @param notify true to notify the nodes of a connection change, false to not notify
     */
    void rebuild(@Nullable BlockPos start, boolean notify);

    /**
     * Rebuilds the network graph.
     */
    default void rebuild() {
        rebuild(null, true);
    }

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
