package com.refinedmods.refinedstorage.api.network.node

import net.minecraft.util.Identifier


/**
 * This registry holds factories for reading and writing network nodes from and to NBT.
 */
interface INetworkNodeRegistry {
    /**
     * Adds a factory to the registry.
     *
     * @param id      the id, as specified in [INetworkNode.getId]
     * @param factory the factory
     */
    fun add(id: Identifier, factory: INetworkNodeFactory)

    /**
     * Returns a factory from the registry.
     *
     * @param id the id, as specified in [INetworkNode.getId]
     * @return the factory, or null if no factory was found
     */
    operator fun get(id: Identifier): INetworkNodeFactory?
}