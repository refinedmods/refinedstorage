package com.refinedmods.refinedstorage.capability

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList


interface BaseInventory : Inventory {
    fun getItems(): DefaultedList<ItemStack>
    fun getMaxSize(): Int

    override fun clear() = getItems().clear()
    override fun size(): Int = getItems().size
    override fun isEmpty(): Boolean = getItems().isEmpty()
    override fun getStack(slot: Int): ItemStack = getItems()[slot]

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        val result = Inventories.splitStack(getItems(), slot, amount)
        if (!result.isEmpty) {
            markDirty()
        }
        return result
    }

    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(getItems(), slot)

    override fun setStack(slot: Int, stack: ItemStack) {
        getItems()[slot] = stack
        if (stack.count > maxCountPerStack) {
            stack.count = maxCountPerStack
        }
    }

    override fun markDirty() {}

    override fun canPlayerUse(player: PlayerEntity): Boolean = true
}