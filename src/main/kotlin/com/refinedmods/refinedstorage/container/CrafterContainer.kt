package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.tile.CrafterTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler

class CrafterContainer(crafter: CrafterTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.CRAFTER, crafter, player, windowId) {
    init {
        for (i in 0..8) {
            addSlot(SlotItemHandler(crafter.getNode().patternItems, i, 8 + 18 * i, 20))
        }
        for (i in 0..3) {
            addSlot(SlotItemHandler(crafter.getNode().getUpgrades(), i, 187, 6 + i * 18))
        }
        addPlayerInventory(8, 55)
        transferManager.addBiTransfer(player.inventory, crafter.getNode().getUpgrades())
        transferManager.addBiTransfer(player.inventory, crafter.getNode().patternItems)
    }
}