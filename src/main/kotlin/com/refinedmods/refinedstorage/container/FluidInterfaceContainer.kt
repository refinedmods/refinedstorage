package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.tile.FluidInterfaceTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler

class FluidInterfaceContainer(fluidInterface: FluidInterfaceTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.FLUID_INTERFACE, fluidInterface, player, windowId) {
    init {
        for (i in 0..3) {
            addSlot(SlotItemHandler(fluidInterface.getNode().getUpgrades(), i, 187, 6 + i * 18))
        }
        addSlot(SlotItemHandler(fluidInterface.getNode().`in`, 0, 44, 32))
        addSlot(FluidFilterSlot(fluidInterface.getNode().getOut(), 0, 116, 32, FilterSlot.Companion.FILTER_ALLOW_SIZE))
        addPlayerInventory(8, 122)
        transferManager.addBiTransfer(player.inventory, fluidInterface.getNode().`in`)
        transferManager.addFluidFilterTransfer(player.inventory, fluidInterface.getNode().getOut())
    }
}