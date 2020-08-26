package com.refinedmods.refinedstorage.apiimpl.storage.disk

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.FluidStorageDiskFactory
import com.refinedmods.refinedstorage.extensions.safeAdd
import com.refinedmods.refinedstorage.extensions.safeSubtract
import com.refinedmods.refinedstorage.util.StackUtils.copy
import net.minecraft.fluid.Fluid
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import reborncore.common.fluid.FluidValue
import reborncore.common.fluid.container.FluidInstance

class FluidStorageDisk(
        private val world: ServerWorld?, 
        override val capacity: Int,
        override val factoryId: Identifier = FluidStorageDiskFactory.ID
) : IStorageDisk<FluidInstance> {
    val rawStacks: Multimap<Fluid, FluidInstance> = ArrayListMultimap.create()

    private var listener: IStorageDiskListener? = null
    private var context: IStorageDiskContainerContext? = null
    override fun writeToNbt(): CompoundTag {
        val tag = CompoundTag()
        val list = ListTag()
        for (stack in rawStacks.values()) {
            list.add(stack.write())
        }
        tag.putInt(NBT_VERSION, VERSION)
        tag.put(NBT_FLUIDS, list)
        tag.putInt(NBT_CAPACITY, capacity)
        return tag
    }

    override fun getStacks(): Collection<FluidInstance> {
        return rawStacks.values()
    }

    override fun insert(stack: FluidInstance, size: Int, action: Action): FluidInstance {
        if (stack.isEmpty) return stack

        for (otherStack in rawStacks[stack.fluid]) {
            if (otherStack.isFluidEqual(stack)) {
                return if (capacity != -1 && stored + size > capacity) {
                    val remainingSpace = capacity - stored
                    if (remainingSpace <= 0) {
                        return copy(stack, size)
                    }

                    if (action == Action.PERFORM) {
                        otherStack.amount = otherStack.amount.safeAdd(FluidValue.fromRaw(remainingSpace))
                        onChanged()
                    }

                    copy(otherStack, size - remainingSpace)
                } else {
                    if (action == Action.PERFORM) {
                        otherStack.amount = otherStack.amount.safeAdd(FluidValue.fromRaw(size))
                        onChanged()
                    }
                    FluidInstance.EMPTY
                }
            }
        }

        return if (capacity != -1 && stored + size > capacity) {
            val remainingSpace = capacity - stored
            if (remainingSpace <= 0) {
                return copy(stack, size)
            }
            if (action == Action.PERFORM) {
                rawStacks.put(stack.fluid, copy(stack, remainingSpace))
                onChanged()
            }
            copy(stack, size - remainingSpace)
        } else {
            if (action == Action.PERFORM) {
                rawStacks.put(stack.fluid, copy(stack, size))
                onChanged()
            }
            FluidInstance.EMPTY
        }
    }

    override fun extract(stack: FluidInstance, size: Int, flags: Int, action: Action): FluidInstance {
        if (stack.isEmpty) return stack

        rawStacks[stack.fluid].forEach { otherStack ->
            if (instance().comparer.isEqual(otherStack, stack, flags)) {
                if (action == Action.PERFORM) {
                    val newVal = otherStack.amount.safeSubtract(FluidValue.fromRaw(size))

                    if (newVal.rawValue == 0) {
                        rawStacks.remove(otherStack.fluid, otherStack)
                    } else {
                        otherStack.amount = newVal
                    }

                    onChanged()
                }

                return copy(otherStack, size)
            }
        }
        
        return FluidInstance.EMPTY
    }

    override fun getStored(): Int {
        return rawStacks
                .values()
                .map { it.amount.rawValue }
                .sum()
    }

    override fun getPriority(): Int {
        return 0
    }

    override fun getAccessType(): AccessType {
        return context!!.accessType
    }

    override fun getCacheDelta(storedPreInsertion: Int, size: Int, remainder: FluidInstance?): Int {
        if (accessType == AccessType.INSERT) {
            return 0
        }
        return if (remainder == null) {
            size
        } else {
            size - remainder.amount.rawValue
        }
    }

    override fun setSettings(listener: IStorageDiskListener?, context: IStorageDiskContainerContext) {
        this.listener = listener
        this.context = context
    }
    
    private fun onChanged() {
        if (listener != null) {
            listener!!.onChanged()
        }
        if (world != null) {
            instance().getStorageDiskManager(world).markForSaving()
        }
    }

    companion object {
        const val NBT_VERSION = "Version"
        const val NBT_CAPACITY = "Capacity"
        const val NBT_FLUIDS = "Fluids"
        const val VERSION = 1
    }
}