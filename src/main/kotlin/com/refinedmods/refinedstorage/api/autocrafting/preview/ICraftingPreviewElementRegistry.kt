package com.refinedmods.refinedstorage.api.autocrafting.preview

import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import java.util.function.Function

/**
 * This registry holds factories for crafting preview elements (for serialization and deserialization over the network).
 */
interface ICraftingPreviewElementRegistry {
    /**
     * Adds a factory to the registry.
     *
     * @param id      the id, as specified in [ICraftingPreviewElement.getId]
     * @param factory the factory
     */
    fun add(id: Identifier, factory: Function<PacketByteBuf, ICraftingPreviewElement<*>>)

    /**
     * Returns a factory from the registry.
     *
     * @param id the id, as specified in [ICraftingPreviewElement.getId]
     * @return the factory, or null if no factory was found
     */
    operator fun get(id: Identifier): Function<PacketByteBuf, ICraftingPreviewElement<*>>?
}