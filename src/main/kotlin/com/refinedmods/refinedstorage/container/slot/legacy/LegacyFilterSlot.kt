package com.refinedmods.refinedstorage.container.slot.legacy

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

class LegacyFilterSlot(inventory: IInventory?, inventoryIndex: Int, x: Int, y: Int) : LegacyBaseSlot(inventory, inventoryIndex, x, y) {
    fun canTakeStack(player: PlayerEntity?): Boolean {
        return false
    }

    fun isItemValid(stack: ItemStack?): Boolean {
        return true
    }

    fun putStack(@Nonnull stack: ItemStack) {
        if (!stack.isEmpty) {
            stack.count = 1
        }
        super.putStack(stack)
    }
}