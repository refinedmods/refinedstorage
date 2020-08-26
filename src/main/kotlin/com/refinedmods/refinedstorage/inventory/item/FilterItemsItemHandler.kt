package com.refinedmods.refinedstorage.inventory.item

import com.refinedmods.refinedstorage.util.StackUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.NonNullList
import net.minecraftforge.items.ItemStackHandler

class FilterItemsItemHandler(private val stack: ItemStack) : ItemStackHandler(27) {
    protected fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        if (!stack.hasTag()) {
            stack.tag = CompoundTag()
        }
        StackUtils.writeItems(this, 0, stack.tag)
    }

    val filteredItems: NonNullList<ItemStack>
        get() = stacks

    init {
        if (stack.hasTag()) {
            StackUtils.readItems(this, 0, stack.tag)
        }
    }
}