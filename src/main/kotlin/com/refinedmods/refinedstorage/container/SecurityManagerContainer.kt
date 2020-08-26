package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.tile.SecurityManagerTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler

class SecurityManagerContainer(securityManager: SecurityManagerTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.SECURITY_MANAGER, securityManager, player, windowId) {
    init {
        var x = 8
        var y = 20
        for (i in 0 until 9 * 2) {
            addSlot(SlotItemHandler(securityManager.getNode().cardsItems, i, x, y))
            if ((i + 1) % 9 == 0) {
                x = 8
                y += 18
            } else {
                x += 18
            }
        }
        addSlot(SlotItemHandler(securityManager.getNode().getEditCard(), 0, 80, 70))
        addPlayerInventory(8, 152)
        transferManager.addBiTransfer(player.inventory, securityManager.getNode().cardsItems)
        transferManager.addTransfer(securityManager.getNode().getEditCard(), player.inventory)
    }
}