package com.refinedmods.refinedstorage.container.slot.filter

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import net.minecraft.item.ItemStack

class DisabledFluidFilterSlot : FluidFilterSlot {
    constructor(inventory: FluidInventory, inventoryIndex: Int, x: Int, y: Int, flags: Int) : super(inventory, inventoryIndex, x, y, flags) {}
    constructor(inventory: FluidInventory, inventoryIndex: Int, x: Int, y: Int) : super(inventory, inventoryIndex, x, y) {}

    override fun onContainerClicked(@Nonnull stack: ItemStack?) {
        // NO OP
    }
}