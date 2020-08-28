package com.refinedmods.refinedstorage.api.network

import net.minecraft.util.math.BlockPos

/**
 * This is a registry for network nodes in the world.
 */
interface INetworkManager {
    /**
     * Gets a network from the registry at a given position.
     *
     * @param pos the position of the network
     * @return the network at the given position, or null if no network was found
     */
    fun getNetwork(pos: BlockPos): INetwork?

    /**
     * Removes a network from the registry at a given position.
     *
     * @param pos the position of the network
     */
    fun removeNetwork(pos: BlockPos)

    /**
     * Sets a network in the registry at a given position.
     *
     * @param pos  the position of the network
     * @param node the node
     */
    fun setNetwork(pos: BlockPos, node: INetwork)

    /**
     * @return all networks in this registry
     */
    fun all(): Collection<INetwork>

    /**
     * Marks the network manager for saving.
     */
    fun markForSaving()
}