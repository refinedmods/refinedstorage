package com.refinedmods.refinedstorage.apiimpl.network

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeFactory
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeRegistry
import net.minecraft.util.Identifier

class NetworkNodeRegistry : INetworkNodeRegistry {
    private val factories: MutableMap<Identifier, INetworkNodeFactory> = HashMap()
    override fun add(id: Identifier, factory: INetworkNodeFactory) {
        factories[id] = factory
    }

    override operator fun get(id: Identifier): INetworkNodeFactory? {
        return factories[id]
    }
}