package com.raoulvdberge.refinedstorage.api.network.node;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.function.Function;

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
    void add(String id, Function<NBTTagCompound, INetworkNode> factory);

    /**
     * Returns a factory from the registry.
     *
     * @param id the id, as specified in {@link INetworkNode#getId()}
     * @return the factory, or null if no factory was found
     */
    @Nullable
    Function<NBTTagCompound, INetworkNode> get(String id);
}
