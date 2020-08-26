package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.tile.ExternalStorageTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import java.util.function.Supplier

class ExternalStorageContainer(externalStorage: ExternalStorageTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.EXTERNAL_STORAGE, externalStorage, player, windowId) {
    init {
        for (i in 0..8) {
            addSlot(FilterSlot(externalStorage.getNode().getItemFilters(), i, 8 + 18 * i, 20).setEnableHandler { externalStorage.getNode().getType() == IType.ITEMS })
        }
        for (i in 0..8) {
            addSlot(FluidFilterSlot(externalStorage.getNode().getFluidFilters(), i, 8 + 18 * i, 20).setEnableHandler { externalStorage.getNode().getType() == IType.FLUIDS })
        }
        addPlayerInventory(8, 141)
        transferManager.addFilterTransfer(player.inventory, externalStorage.getNode().getItemFilters(), externalStorage.getNode().getFluidFilters(), Supplier { externalStorage.getNode().getType() })
    }
}