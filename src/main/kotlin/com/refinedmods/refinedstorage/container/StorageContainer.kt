package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.tile.StorageTile
import net.minecraft.entity.player.PlayerEntity

class StorageContainer(storage: StorageTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.STORAGE_BLOCK, storage, player, windowId) {
    init {
        for (i in 0..8) {
            addSlot(FilterSlot(storage.getNode().filters, i, 8 + 18 * i, 20))
        }
        addPlayerInventory(8, 141)
        transferManager.addItemFilterTransfer(player.inventory, storage.getNode().filters)
    }
}