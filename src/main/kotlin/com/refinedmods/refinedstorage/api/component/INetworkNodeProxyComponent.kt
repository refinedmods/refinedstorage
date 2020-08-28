package com.refinedmods.refinedstorage.api.component

import nerdhub.cardinal.components.api.component.Component
import net.minecraft.nbt.CompoundTag

interface INetworkNodeProxyComponent: Component {
    companion object{
        const val ID = "network_node_proxy_component"
    }

    override fun fromTag(tag: CompoundTag) {}

    override fun toTag(tag: CompoundTag): CompoundTag {
        return tag
    }
}

class NetworkNodeProxyComponent: INetworkNodeProxyComponent