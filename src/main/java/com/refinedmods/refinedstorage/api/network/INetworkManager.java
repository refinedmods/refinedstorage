package com.refinedmods.refinedstorage.api.network;

import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * This is a registry for network nodes in the world.
 */
public interface INetworkManager {
    /**
     * Gets a network from the registry at a given position.
     *
     * @param pos the position of the network
     * @return the network at the given position, or null if no network was found
     */
    @Nullable
    INetwork getNetwork(BlockPos pos);

    /**
     * Removes a network from the registry at a given position.
     *
     * @param pos the position of the network
     */
    void removeNetwork(BlockPos pos);

    /**
     * Sets a network in the registry at a given position.
     *
     * @param pos  the position of the network
     * @param node the node
     */
    void setNetwork(BlockPos pos, INetwork node);

    /**
     * @return all networks in this registry
     */
    Collection<INetwork> all();

    /**
     * Marks the network manager for saving.
     */
    void markForSaving();
}
