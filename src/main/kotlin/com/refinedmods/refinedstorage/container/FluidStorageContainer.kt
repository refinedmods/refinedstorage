package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.tile.FluidStorageTile
import net.minecraft.entity.player.PlayerEntity

class FluidStorageContainer(fluidStorage: FluidStorageTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.FLUID_STORAGE_BLOCK, fluidStorage, player, windowId) {
    init {
        for (i in 0..8) {
            addSlot(FluidFilterSlot(fluidStorage.getNode().filters, i, 8 + 18 * i, 20))
        }
        addPlayerInventory(8, 141)
        transferManager.addFluidFilterTransfer(player.inventory, fluidStorage.getNode().filters)
    }
}