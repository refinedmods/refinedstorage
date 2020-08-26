package com.refinedmods.refinedstorage.inventory.listener

import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler

class NetworkNodeInventoryListener(private val node: INetworkNode) : InventoryListener<BaseItemHandler?> {
    override fun onChanged(handler: BaseItemHandler?, slot: Int, reading: Boolean) {
        if (!reading) {
            node.markDirty()
        }
    }
}