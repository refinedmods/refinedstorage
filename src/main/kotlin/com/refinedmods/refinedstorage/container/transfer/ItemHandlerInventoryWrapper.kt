package com.refinedmods.refinedstorage.container.transfer

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import java.util.*

internal class ItemHandlerInventoryWrapper(handler: IItemHandler?) : IInventoryWrapper {
    private val handler: IItemHandler?
    override fun insert(stack: ItemStack?): InsertionResult? {
        return InsertionResult(ItemHandlerHelper.insertItem(handler, stack, false))
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val that = o as ItemHandlerInventoryWrapper
        return handler == that.handler
    }

    override fun hashCode(): Int {
        return Objects.hash(handler)
    }

    init {
        this.handler = handler
    }
}