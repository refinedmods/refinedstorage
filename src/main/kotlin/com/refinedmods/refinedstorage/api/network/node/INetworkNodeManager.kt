package com.refinedmods.refinedstorage.api.network.node

import net.minecraft.util.math.BlockPos

/**
 * This is a registry for network nodes in the world.
 */
interface INetworkNodeManager {
    /**
     * Gets a network node from the registry at a given position.
     *
     * @param pos the position of the node
     * @return the network node at the given position, or null if no network node was found
     */
    fun getNode(pos: BlockPos): INetworkNode?

    /**
     * Removes a node from the registry at a given position.
     *
     * @param pos the position of the node
     */
    fun removeNode(pos: BlockPos)

    /**
     * Sets a node in the registry at a given position.
     *
     * @param pos  the position of the node
     * @param node the node
     */
    fun setNode(pos: BlockPos, node: INetworkNode)

    /**
     * @return all nodes in this registry
     */
    fun all(): Collection<INetworkNode>

    /**
     * Marks the network node manager for saving.
     */
    fun markForSaving()
}