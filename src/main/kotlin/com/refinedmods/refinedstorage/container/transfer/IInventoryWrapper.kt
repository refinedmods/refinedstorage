package com.refinedmods.refinedstorage.container.transfer

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.item.ItemStack

internal interface IInventoryWrapper {
    fun insert(stack: ItemStack?): InsertionResult?
}