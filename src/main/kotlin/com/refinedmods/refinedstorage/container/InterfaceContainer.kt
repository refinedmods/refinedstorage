package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.OutputSlot
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.tile.InterfaceTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler

class InterfaceContainer(tile: InterfaceTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.INTERFACE, tile, player, windowId) {
    init {
        for (i in 0..8) {
            addSlot(SlotItemHandler(tile.getNode().getImportItems(), i, 8 + 18 * i, 20))
        }
        for (i in 0..8) {
            addSlot(FilterSlot(tile.getNode().getExportFilterItems(), i, 8 + 18 * i, 54, FilterSlot.Companion.FILTER_ALLOW_SIZE))
        }
        for (i in 0..8) {
            addSlot(OutputSlot(tile.getNode().getExportItems(), i, 8 + 18 * i, 100))
        }
        for (i in 0..3) {
            addSlot(SlotItemHandler(tile.getNode().getUpgrades(), i, 187, 6 + i * 18))
        }
        addPlayerInventory(8, 134)
        transferManager.addBiTransfer(player.inventory, tile.getNode().getUpgrades())
        transferManager.addBiTransfer(player.inventory, tile.getNode().getImportItems())
        transferManager.addTransfer(tile.getNode().getExportItems(), player.inventory)
    }
}