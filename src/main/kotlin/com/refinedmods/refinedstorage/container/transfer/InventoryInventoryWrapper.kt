package com.refinedmods.refinedstorage.container.transfer

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.wrapper.InvWrapper
import net.minecraftforge.items.wrapper.RangedWrapper
import java.util.*

internal class InventoryInventoryWrapper(inventory: IInventory) : IInventoryWrapper {
    private val inventory: IInventory
    private var wrapper: IItemHandler? = null
    override fun insert(stack: ItemStack?): InsertionResult? {
        return InsertionResult(ItemHandlerHelper.insertItem(wrapper, stack, false))
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val that = o as InventoryInventoryWrapper
        return inventory == that.inventory
    }

    override fun hashCode(): Int {
        return Objects.hash(inventory)
    }

    init {
        this.inventory = inventory
        if (inventory is PlayerInventory) {
            // Don't use PlayerMainInvWrapper to avoid stack animations.
            wrapper = RangedWrapper(InvWrapper(inventory), 0, (inventory as PlayerInventory).mainInventory.size())
        } else {
            wrapper = InvWrapper(inventory)
        }
    }
}