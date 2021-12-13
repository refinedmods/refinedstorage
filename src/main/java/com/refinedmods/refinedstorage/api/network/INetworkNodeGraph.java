package com.refinedmods.refinedstorage.api.network;

import com.refinedmods.refinedstorage.api.util.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Represents a graph of all the nodes connected to a network.
 */
public interface INetworkNodeGraph {
    /**
     * Rebuilds the network graph.
     *
     * @param action whether to perform or simulate
     * @param level  the origin level
     * @param origin the origin, usually the network position
     */
    void invalidate(Action action, Level level, BlockPos origin);

    /**
     * Runs an action on the network.
     * If the network is rebuilding it's graph, the action will be executed after the graph was built.
     *
     * @param handler the action to run
     */
    void runActionWhenPossible(Consumer<INetwork> handler);

    /**
     * @return a collection of all connected entries
     */
    Collection<INetworkNodeGraphEntry> all();

    /**
     * @param listener the listener
     */
    void addListener(INetworkNodeGraphListener listener);

    /**
     * Disconnects and notifies all connected nodes.
     */
    void disconnectAll();
}
