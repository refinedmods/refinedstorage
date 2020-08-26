package com.refinedmods.refinedstorage.container.slot

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

class DisabledSlot(itemHandler: IItemHandler?, inventoryIndex: Int, x: Int, y: Int) : BaseSlot(itemHandler, inventoryIndex, x, y) {
    fun isItemValid(@Nonnull stack: ItemStack?): Boolean {
        return false
    }
}