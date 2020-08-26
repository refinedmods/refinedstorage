package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.DisabledFluidFilterSlot
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.fluids.FluidInstance

class FluidAmountContainer(player: PlayerEntity, stack: FluidInstance?) : BaseContainer(null, null, player, 0) {
    init {
        val inventory = FluidInventory(1)
        inventory.setFluid(0, stack)
        addSlot(DisabledFluidFilterSlot(inventory, 0, 89, 48, 0))
    }
}