package com.refinedmods.refinedstorage.container.slot

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider
import net.minecraftforge.items.IItemHandler

class CrafterManagerSlot(itemHandler: IItemHandler?, inventoryIndex: Int, x: Int, y: Int, private val visible: Boolean, private val display: IScreenInfoProvider?, private val crafterManager: CrafterManagerNetworkNode) : BaseSlot(itemHandler, inventoryIndex, x, y) {
    override val isEnabled: Boolean
        get() = yPos >= display!!.topHeight && yPos < display.topHeight + 18 * display.visibleRows && visible && crafterManager.isActiveOnClient
}