package com.raoulvdberge.refinedstorage.api.network.node;

import javax.annotation.Nullable;

/**
 * This registry holds factories for reading and writing network nodes from and to NBT.
 */
public interface INetworkNodeRegistry {
    /**
     * Adds a factory to the registry.
     *
     * @param id      the id, as specified in {@link INetworkNode#getId()}
     * @param factory the factory
     */
    void add(String id, INetworkNodeFactory factory);

    /**
     * Returns a factory from the registry.
     *
     * @param id the id, as specified in {@link INetworkNode#getId()}
     * @return the factory, or null if no factory was found
     */
    @Nullable
    INetworkNodeFactory get(String id);
}
