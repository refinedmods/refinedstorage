package com.raoulvdberge.refinedstorage.api.network.node;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * This is a registry for network nodes in the world.
 */
public interface INetworkNodeManager {
    /**
     * Gets a network node from the registry at a given position.
     *
     * @param pos the position
     * @return the network node at the given position, or null if no network node was found
     */
    @Nullable
    INetworkNode getNode(BlockPos pos);

    /**
     * Removes a node from the registry at a given position.
     *
     * @param pos the position
     */
    void removeNode(BlockPos pos);

    /**
     * Sets a node in the registry at a given position.
     *
     * @param pos  the position
     * @param node the node
     */
    void setNode(BlockPos pos, INetworkNode node);

    /**
     * @return all nodes in this registry
     */
    Collection<INetworkNode> all();

    /**
     * Clears all the nodes.
     */
    void clear();
}
