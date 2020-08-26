package com.refinedmods.refinedstorage.container.transfer

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper

internal class ItemFilterInventoryWrapper(filterInv: IItemHandlerModifiable) : IInventoryWrapper {
    private val filterInv: IItemHandlerModifiable
    override fun insert(stack: ItemStack?): InsertionResult? {
        val stop = InsertionResult(InsertionResultType.STOP)
        for (i in 0 until filterInv.getSlots()) {
            if (instance().getComparer()!!.isEqualNoQuantity(filterInv.getStackInSlot(i), stack!!)) {
                return stop
            }
        }
        for (i in 0 until filterInv.getSlots()) {
            if (filterInv.getStackInSlot(i).isEmpty()) {
                filterInv.setStackInSlot(i, ItemHandlerHelper.copyStackWithSize(stack, 1))
                break
            }
        }
        return stop
    }

    init {
        this.filterInv = filterInv
    }
}