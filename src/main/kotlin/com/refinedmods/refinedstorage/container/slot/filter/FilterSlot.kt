package com.refinedmods.refinedstorage.container.slot.filter

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.BaseSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

class FilterSlot(handler: IItemHandler?, inventoryIndex: Int, x: Int, y: Int, private val flags: Int) : BaseSlot(handler, inventoryIndex, x, y) {
    constructor(handler: IItemHandler?, inventoryIndex: Int, x: Int, y: Int) : this(handler, inventoryIndex, x, y, 0) {}

    fun isItemValid(@Nonnull stack: ItemStack): Boolean {
        return if (super.isItemValid(stack)) {
            if (isBlockAllowed) {
                stack.item is BlockItem
            } else true
        } else false
    }

    fun putStack(@Nonnull stack: ItemStack) {
        if (!stack.isEmpty && !isSizeAllowed) {
            stack.count = 1
        }
        super.putStack(stack)
    }

    fun canTakeStack(playerIn: PlayerEntity?): Boolean {
        return false
    }

    val isSizeAllowed: Boolean
        get() = flags and FILTER_ALLOW_SIZE == FILTER_ALLOW_SIZE
    val isBlockAllowed: Boolean
        get() = flags and FILTER_ALLOW_BLOCKS == FILTER_ALLOW_BLOCKS
    val isAlternativesAllowed: Boolean
        get() = flags and FILTER_ALLOW_ALTERNATIVES == FILTER_ALLOW_ALTERNATIVES

    companion object {
        const val FILTER_ALLOW_SIZE = 1
        const val FILTER_ALLOW_BLOCKS = 2
        const val FILTER_ALLOW_ALTERNATIVES = 4
    }
}