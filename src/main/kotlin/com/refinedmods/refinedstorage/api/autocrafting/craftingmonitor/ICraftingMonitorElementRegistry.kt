package com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor

import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import java.util.function.Function

/**
 * This registry holds factories for crafting monitor elements (for serialization and deserialization over the network).
 */
interface ICraftingMonitorElementRegistry {
    /**
     * Adds a factory to the registry.
     *
     * @param id      the id, as specified in [ICraftingMonitorElement.getId]
     * @param factory the factory
     */
    fun add(id: Identifier, factory: Function<PacketByteBuf, ICraftingMonitorElement>)

    /**
     * Returns a factory from the registry.
     *
     * @param id the id, as specified in [ICraftingMonitorElement.getId]
     * @return the factory, or null if no factory was found
     */
    operator fun get(id: Identifier): Function<PacketByteBuf, ICraftingMonitorElement>?
}