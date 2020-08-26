package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview

import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import java.util.function.Function

class CraftingPreviewElementRegistry : ICraftingPreviewElementRegistry {
    private val registry: MutableMap<Identifier, Function<PacketByteBuf, ICraftingPreviewElement<*>>> = HashMap()
    override fun add(id: Identifier, factory: Function<PacketByteBuf, ICraftingPreviewElement<*>>) {
        registry[id] = factory
    }

    override operator fun get(id: Identifier): Function<PacketByteBuf, ICraftingPreviewElement<*>>? {
        return registry[id]
    }
}