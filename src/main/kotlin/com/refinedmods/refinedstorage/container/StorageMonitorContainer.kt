package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.tile.StorageMonitorTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import java.util.function.Supplier

class StorageMonitorContainer(storageMonitor: StorageMonitorTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.STORAGE_MONITOR, storageMonitor, player, windowId) {
    init {
        addSlot(FilterSlot(storageMonitor.getNode().itemFilters, 0, 80, 20).setEnableHandler { storageMonitor.getNode().getType() == IType.ITEMS })
        addSlot(FluidFilterSlot(storageMonitor.getNode().fluidFilters, 0, 80, 20).setEnableHandler { storageMonitor.getNode().getType() == IType.FLUIDS })
        addPlayerInventory(8, 55)
        transferManager.addFilterTransfer(player.inventory, storageMonitor.getNode().itemFilters, storageMonitor.getNode().fluidFilters, Supplier { storageMonitor.getNode().getType() })
    }
}