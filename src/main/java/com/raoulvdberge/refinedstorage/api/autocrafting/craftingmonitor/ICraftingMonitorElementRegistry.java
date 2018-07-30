package com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * This registry holds factories for crafting monitor elements (for serialization and deserialization over the network).
 */
public interface ICraftingMonitorElementRegistry {
    /**
     * Adds a factory to the registry.
     *
     * @param id      the id, as specified in {@link ICraftingMonitorElement#getId()}
     * @param factory the factory
     */
    void add(String id, Function<ByteBuf, ICraftingMonitorElement> factory);

    /**
     * Returns a factory from the registry.
     *
     * @param id the id, as specified in {@link ICraftingMonitorElement#getId()}
     * @return the factory, or null if no factory was found
     */
    @Nullable
    Function<ByteBuf, ICraftingMonitorElement> get(String id);
}
