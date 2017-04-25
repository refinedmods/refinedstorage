package com.raoulvdberge.refinedstorage.api.network.node;

import javax.annotation.Nonnull;

/**
 * Makes a network node accessible from a tile entity. Implement this as a capability.
 *
 * @param <T> the network node
 */
public interface INetworkNodeProxy<T extends INetworkNode> {
    /**
     * @return the node
     */
    @Nonnull
    T getNode();
}
