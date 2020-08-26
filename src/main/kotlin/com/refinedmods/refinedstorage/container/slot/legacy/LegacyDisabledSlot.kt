package com.refinedmods.refinedstorage.container.slot.legacy

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

class LegacyDisabledSlot(inventory: IInventory?, inventoryIndex: Int, x: Int, y: Int) : LegacyBaseSlot(inventory, inventoryIndex, x, y) {
    fun isItemValid(@Nonnull stack: ItemStack?): Boolean {
        return false
    }
}