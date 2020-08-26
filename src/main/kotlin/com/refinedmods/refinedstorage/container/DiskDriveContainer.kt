package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.tile.DiskDriveTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler
import java.util.function.Supplier

class DiskDriveContainer(diskDrive: DiskDriveTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.DISK_DRIVE, diskDrive, player, windowId) {
    init {
        val x = 80
        val y = 54
        for (i in 0..7) {
            addSlot(SlotItemHandler(diskDrive.getNode().getDisks(), i, x + i % 2 * 18, y + Math.floorDiv(i, 2) * 18))
        }
        for (i in 0..8) {
            addSlot(FilterSlot(diskDrive.getNode().getItemFilters(), i, 8 + 18 * i, 20).setEnableHandler { diskDrive.getNode().getType() == IType.ITEMS })
        }
        for (i in 0..8) {
            addSlot(FluidFilterSlot(diskDrive.getNode().getFluidFilters(), i, 8 + 18 * i, 20).setEnableHandler { diskDrive.getNode().getType() == IType.FLUIDS })
        }
        addPlayerInventory(8, 141)
        transferManager.addBiTransfer(player.inventory, diskDrive.getNode().getDisks())
        transferManager.addFilterTransfer(player.inventory, diskDrive.getNode().getItemFilters(), diskDrive.getNode().getFluidFilters(), Supplier { diskDrive.getNode().getType() })
    }
}