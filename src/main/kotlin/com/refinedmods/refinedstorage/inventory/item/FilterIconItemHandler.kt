package com.refinedmods.refinedstorage.inventory.item

import com.refinedmods.refinedstorage.item.FilterItem
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler

class FilterIconItemHandler(private val stack: ItemStack) : ItemStackHandler(1) {
    protected fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        FilterItem.setIcon(stack, getStackInSlot(0))
    }

    init {
        setStackInSlot(0, FilterItem.getIcon(stack))
    }
}