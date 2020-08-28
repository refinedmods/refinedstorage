package com.refinedmods.refinedstorage.apiimpl.util

import com.google.common.collect.ArrayListMultimap
import com.refinedmods.refinedstorage.api.util.IStackList
import com.refinedmods.refinedstorage.api.util.StackListEntry
import com.refinedmods.refinedstorage.api.util.StackListResult
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import net.minecraft.fluid.Fluid
import reborncore.common.fluid.FluidValue
import reborncore.common.fluid.container.FluidInstance
import java.util.*

@Suppress("DEPRECATION")
class FluidInstanceList(
        private val stackMap: ArrayListMultimap<Fluid, StackListEntry<FluidInstance>> = ArrayListMultimap.create<Fluid, StackListEntry<FluidInstance>>(),
        private val index: MutableMap<UUID, FluidInstance> = mutableMapOf()
): IStackList<FluidInstance> {

    override fun add(stack: FluidInstance, size: Int): StackListResult<FluidInstance> {
        return add(stack, FluidValue.fromRaw(size))
    }

    fun add(stack: FluidInstance, size: FluidValue): StackListResult<FluidInstance> {
        require(!(stack.isEmpty || size.isEmpty)) { "Cannot accept empty stack" }

        stackMap[stack.fluid].forEach { entry ->
            val otherStack = entry.stack
            val oldAmount = otherStack.amount.rawValue
            if (stack.isFluidEqual(otherStack)) {
                otherStack.amount = otherStack.amount.add(size)

                return StackListResult(otherStack, entry.id, otherStack.amount.rawValue - oldAmount)
            }
        }

        val newStack: FluidInstance = stack.copy()
        newStack.amount = size

        val newEntry: StackListEntry<FluidInstance> = StackListEntry(newStack)
        stackMap.put(newStack.fluid, newEntry)
        index[newEntry.id] = newEntry.stack

        return StackListResult(newStack, newEntry.id, size.rawValue)
    }

    override fun add(stack: FluidInstance): StackListResult<FluidInstance> {
        return add(stack, stack.amount.rawValue)
    }

    override fun remove(stack: FluidInstance, size: Int): StackListResult<FluidInstance>? {
        stackMap[stack.fluid].forEach { entry ->
            val otherStack: FluidInstance = entry.stack
            if (stack.isFluidEqual(otherStack)) {
                return add(stack, FluidValue.fromRaw( 0 - size))
            }
        }

        return null
    }

    fun remove(stack: FluidInstance, amount: FluidValue): StackListResult<FluidInstance>? {
        stackMap[stack.fluid].forEach { entry ->
            val otherStack: FluidInstance = entry.stack
            if (stack.isFluidEqual(otherStack)) {
                return add(stack, FluidValue.fromRaw( 0 - amount.rawValue))
            }
        }

        return null
    }

    override fun remove(stack: FluidInstance): StackListResult<FluidInstance>? {
        return remove(stack, stack.amount)
    }

    override fun getCount(stack: FluidInstance, flags: Int): Int {
        val found: FluidInstance = get(stack, flags) ?: return 0
        return found.amount.rawValue
    }

    override operator fun get(stack: FluidInstance, flags: Int): FluidInstance? {
        for (entry in stackMap[stack.fluid]) {
            val otherStack: FluidInstance = entry.stack
            if (instance().comparer.isEqual(otherStack, stack, flags)) {
                return otherStack
            }
        }
        return null
    }

    override fun getEntry(stack: FluidInstance, flags: Int): StackListEntry<FluidInstance>? {
        for (entry in stackMap[stack.fluid]) {
            val otherStack: FluidInstance = entry.stack
            if (instance().comparer.isEqual(otherStack, stack, flags)) {
                return entry
            }
        }
        return null
    }

    override fun get(id: UUID): FluidInstance? {
        return index[id]
    }

    override fun clear() {
        stackMap.clear()
        index.clear()
    }

    override val isEmpty: Boolean
        get() = stackMap.isEmpty

    override fun getStacks(): Collection<StackListEntry<FluidInstance>> {
        return stackMap.values()
    }

    override fun copy(): IStackList<FluidInstance> {
        val list = FluidInstanceList()
        for (entry in stackMap.values()) {
            val newStack: FluidInstance = entry.stack.copy()
            list.stackMap.put(entry.stack.fluid, StackListEntry(entry.id, newStack))
            list.index[entry.id] = newStack
        }
        return list
    }
}