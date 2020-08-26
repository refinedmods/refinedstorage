package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.tile.ImporterTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler
import java.util.function.Supplier

class ImporterContainer(importer: ImporterTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.IMPORTER, importer, player, windowId) {
    init {
        for (i in 0..3) {
            addSlot(SlotItemHandler(importer.getNode().getUpgrades(), i, 187, 6 + i * 18))
        }
        for (i in 0..8) {
            addSlot(FilterSlot(importer.getNode().getItemFilters(), i, 8 + 18 * i, 20).setEnableHandler { importer.getNode().getType() == IType.ITEMS })
        }
        for (i in 0..8) {
            addSlot(FluidFilterSlot(importer.getNode().getFluidFilters(), i, 8 + 18 * i, 20).setEnableHandler { importer.getNode().getType() == IType.FLUIDS })
        }
        addPlayerInventory(8, 55)
        transferManager.addBiTransfer(player.inventory, importer.getNode().getUpgrades())
        transferManager.addFilterTransfer(player.inventory, importer.getNode().getItemFilters(), importer.getNode().getFluidFilters(), Supplier { importer.getNode().getType() })
    }
}