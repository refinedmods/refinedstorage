package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler
import java.util.function.Supplier

class DiskManipulatorContainer(diskManipulator: DiskManipulatorTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.DISK_MANIPULATOR, diskManipulator, player, windowId) {
    init {
        for (i in 0..3) {
            addSlot(SlotItemHandler(diskManipulator.getNode().getUpgrades(), i, 187, 6 + i * 18))
        }
        for (i in 0..2) {
            addSlot(SlotItemHandler(diskManipulator.getNode().getInputDisks(), i, 44, 57 + i * 18))
        }
        for (i in 0..2) {
            addSlot(SlotItemHandler(diskManipulator.getNode().getOutputDisks(), i, 116, 57 + i * 18))
        }
        for (i in 0..8) {
            addSlot(FilterSlot(diskManipulator.getNode().getItemFilters(), i, 8 + 18 * i, 20).setEnableHandler { diskManipulator.getNode().getType() == IType.ITEMS })
        }
        for (i in 0..8) {
            addSlot(FluidFilterSlot(diskManipulator.getNode().getFluidFilters(), i, 8 + 18 * i, 20).setEnableHandler { diskManipulator.getNode().getType() == IType.FLUIDS })
        }
        addPlayerInventory(8, 129)
        transferManager.addBiTransfer(player.inventory, diskManipulator.getNode().getUpgrades())
        transferManager.addBiTransfer(player.inventory, diskManipulator.getNode().getInputDisks())
        transferManager.addTransfer(diskManipulator.getNode().getOutputDisks(), player.inventory)
        transferManager.addFilterTransfer(player.inventory, diskManipulator.getNode().getItemFilters(), diskManipulator.getNode().getFluidFilters(), Supplier { diskManipulator.getNode().getType() })
    }
}