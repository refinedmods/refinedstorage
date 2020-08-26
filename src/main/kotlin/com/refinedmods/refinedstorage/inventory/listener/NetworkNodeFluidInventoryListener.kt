package com.refinedmods.refinedstorage.inventory.listener

import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory

class NetworkNodeFluidInventoryListener(private val node: INetworkNode) : InventoryListener<FluidInventory?> {
    override fun onChanged(handler: FluidInventory?, slot: Int, reading: Boolean) {
        if (!reading) {
            node.markDirty()
        }
    }
}