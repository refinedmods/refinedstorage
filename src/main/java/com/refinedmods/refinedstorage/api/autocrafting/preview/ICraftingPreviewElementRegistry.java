package com.refinedmods.refinedstorage.api.autocrafting.preview;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * This registry holds factories for crafting preview elements (for serialization and deserialization over the network).
 */
public interface ICraftingPreviewElementRegistry {
    /**
     * Adds a factory to the registry.
     *
     * @param id      the id, as specified in {@link ICraftingPreviewElement#getId()}
     * @param factory the factory
     */
    void add(ResourceLocation id, Function<PacketBuffer, ICraftingPreviewElement<?>> factory);

    /**
     * Returns a factory from the registry.
     *
     * @param id the id, as specified in {@link ICraftingPreviewElement#getId()}
     * @return the factory, or null if no factory was found
     */
    @Nullable
    Function<PacketBuffer, ICraftingPreviewElement<?>> get(ResourceLocation id);
}
