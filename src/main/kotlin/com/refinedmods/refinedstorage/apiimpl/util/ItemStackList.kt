package com.refinedmods.refinedstorage.apiimpl.util

import com.google.common.collect.ArrayListMultimap
import com.refinedmods.refinedstorage.api.util.IStackList
import com.refinedmods.refinedstorage.api.util.StackListEntry
import com.refinedmods.refinedstorage.api.util.StackListResult
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.*


class ItemStackList : IStackList<ItemStack> {
    private val stacks = ArrayListMultimap.create<Item, StackListEntry<ItemStack>>()
    private val index: MutableMap<UUID, ItemStack> = HashMap()
    override fun add(stack: ItemStack, size: Int): StackListResult<ItemStack> {
        require(!(stack.isEmpty || size <= 0)) { "Cannot accept empty stack" }
        for (entry in stacks[stack.item]) {
            val otherStack = entry.stack
            if (instance().comparer.isEqualNoQuantity(otherStack, stack)) {
                if (otherStack.count.toLong() + size.toLong() > Int.MAX_VALUE) {
                    otherStack.count = Int.MAX_VALUE
                } else {
                    otherStack.increment(size)
                }
                return StackListResult(otherStack, entry.id, size)
            }
        }
        val newStack = stack.copy()
        stack.count = size
        val newEntry: StackListEntry<ItemStack> = StackListEntry(newStack)
        stacks.put(stack.item, newEntry)
        index[newEntry.id] = newEntry.stack
        return StackListResult(newEntry.stack, newEntry.id, size)
    }

    override fun add(stack: ItemStack): StackListResult<ItemStack> {
        return add(stack, stack.count)
    }

    override fun remove(stack: ItemStack, size: Int): StackListResult<ItemStack>? {
        for (entry in stacks[stack.item]) {
            val otherStack = entry.stack
            if (instance().comparer.isEqualNoQuantity(otherStack, stack)) {
                return if (otherStack.count - size <= 0) {
                    stacks.remove(otherStack.item, entry)
                    index.remove(entry.id)
                    StackListResult(otherStack, entry.id, -otherStack.count)
                } else {
                    otherStack.decrement(size)
                    StackListResult(otherStack, entry.id, -size)
                }
            }
        }
        return null
    }

    override fun remove(stack: ItemStack): StackListResult<ItemStack>? {
        return remove(stack, stack.count)
    }

    override fun getCount(stack: ItemStack, flags: Int): Int {
        val found = get(stack, flags) ?: return 0
        return found.count
    }

    override operator fun get(stack: ItemStack, flags: Int): ItemStack? {
        for (entry in stacks[stack.item]) {
            val otherStack = entry.stack
            if (instance().comparer.isEqual(otherStack, stack, flags)) {
                return otherStack
            }
        }
        return null
    }

    override fun getEntry(stack: ItemStack, flags: Int): StackListEntry<ItemStack>? {
        for (entry in stacks[stack.item]) {
            val otherStack = entry.stack
            if (instance().comparer.isEqual(otherStack, stack, flags)) {
                return entry
            }
        }
        return null
    }

    override fun get(id: UUID): ItemStack? {
        return index[id]
    }

    override fun clear() {
        stacks.clear()
        index.clear()
    }

    override val isEmpty: Boolean
        get() = stacks.isEmpty

    override fun getStacks(): Collection<StackListEntry<ItemStack>> {
        return stacks.values()
    }

    override fun copy(): IStackList<ItemStack> {
        val list = ItemStackList()
        for (entry in stacks.values()) {
            val newStack = entry.stack.copy()
            list.stacks.put(entry.stack.item, StackListEntry(entry.id, newStack))
            list.index[entry.id] = newStack
        }
        return list
    }
}