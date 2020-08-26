package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.item.UpgradeItem
import com.refinedmods.refinedstorage.tile.ExporterTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler
import java.util.function.Supplier

class ExporterContainer(private val exporter: ExporterTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.EXPORTER, exporter, player, windowId) {
    fun initSlots() {
        this.inventorySlots.clear()
        this.inventoryItemStacks.clear()
        transferManager.clearTransfers()
        for (i in 0..3) {
            addSlot(SlotItemHandler(exporter.getNode().getUpgrades(), i, 187, 6 + i * 18))
        }
        for (i in 0..8) {
            addSlot(FilterSlot(
                    exporter.getNode().getItemFilters(),
                    i,
                    8 + 18 * i,
                    20,
                    if (exporter.getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR)) FilterSlot.Companion.FILTER_ALLOW_SIZE else 0
            ).setEnableHandler { exporter.getNode().getType() == IType.ITEMS })
        }
        for (i in 0..8) {
            addSlot(FluidFilterSlot(
                    exporter.getNode().getFluidFilters(),
                    i,
                    8 + 18 * i,
                    20,
                    if (exporter.getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR)) FluidFilterSlot.Companion.FILTER_ALLOW_SIZE else 0
            ).setEnableHandler { exporter.getNode().getType() == IType.FLUIDS })
        }
        addPlayerInventory(8, 55)
        transferManager.addBiTransfer(player.inventory, exporter.getNode().getUpgrades())
        transferManager.addFilterTransfer(player.inventory, exporter.getNode().getItemFilters(), exporter.getNode().getFluidFilters(), Supplier { exporter.getNode().getType() })
    }

    init {
        initSlots()
    }
}