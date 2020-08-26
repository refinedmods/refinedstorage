package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import java.util.*
import java.util.function.Function


class CraftingMonitorElementRegistry : ICraftingMonitorElementRegistry {
    private val registry: MutableMap<Identifier, Function<PacketByteBuf, ICraftingMonitorElement>> = HashMap()
    override fun add(id: Identifier, factory: Function<PacketByteBuf, ICraftingMonitorElement>) {
        registry[id] = factory
    }

    override fun get(id: Identifier): Function<PacketByteBuf, ICraftingMonitorElement>? {
        return registry[id]
    }
}