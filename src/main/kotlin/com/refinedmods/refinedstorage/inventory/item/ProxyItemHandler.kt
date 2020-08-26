package com.refinedmods.refinedstorage.inventory.item

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

class ProxyItemHandler(insertHandler: IItemHandler, extractHandler: IItemHandler) : IItemHandler {
    private val insertHandler: IItemHandler
    private val extractHandler: IItemHandler
    val slots: Int
        get() = insertHandler.getSlots() + extractHandler.getSlots()

    @Nonnull
    fun getStackInSlot(slot: Int): ItemStack {
        return if (slot < insertHandler.getSlots()) insertHandler.getStackInSlot(slot) else extractHandler.getStackInSlot(slot - insertHandler.getSlots())
    }

    @Nonnull
    fun insertItem(slot: Int, @Nonnull stack: ItemStack?, simulate: Boolean): ItemStack {
        return if (slot < insertHandler.getSlots()) insertHandler.insertItem(slot, stack, simulate) else stack!!
    }

    @Nonnull
    fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return if (slot >= insertHandler.getSlots()) extractHandler.extractItem(slot - insertHandler.getSlots(), amount, simulate) else ItemStack.EMPTY
    }

    fun getSlotLimit(slot: Int): Int {
        return if (slot < insertHandler.getSlots()) insertHandler.getSlotLimit(slot) else extractHandler.getSlotLimit(slot - insertHandler.getSlots())
    }

    fun isItemValid(slot: Int, @Nonnull stack: ItemStack?): Boolean {
        return if (slot < insertHandler.getSlots()) insertHandler.isItemValid(slot, stack) else extractHandler.isItemValid(slot - extractHandler.getSlots(), stack)
    }

    init {
        this.insertHandler = insertHandler
        this.extractHandler = extractHandler
    }
}