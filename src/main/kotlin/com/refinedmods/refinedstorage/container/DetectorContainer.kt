package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.tile.DetectorTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import java.util.function.Supplier

class DetectorContainer(detector: DetectorTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.DETECTOR, detector, player, windowId) {
    init {
        addSlot(FilterSlot(detector.getNode().getItemFilters(), 0, 107, 20).setEnableHandler { detector.getNode().getType() == IType.ITEMS })
        addSlot(FluidFilterSlot(detector.getNode().getFluidFilters(), 0, 107, 20).setEnableHandler { detector.getNode().getType() == IType.FLUIDS })
        addPlayerInventory(8, 55)
        transferManager.addFilterTransfer(player.inventory, detector.getNode().getItemFilters(), detector.getNode().getFluidFilters(), Supplier { detector.getNode().getType() })
    }
}