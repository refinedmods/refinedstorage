package com.refinedmods.refinedstorage.apiimpl.storage.disk

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.ItemStorageDiskFactory
import com.refinedmods.refinedstorage.extensions.safeAdd
import com.refinedmods.refinedstorage.extensions.safeSubtract
import com.refinedmods.refinedstorage.util.StackUtils.serializeStackToNbt
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier

class ItemStorageDisk(
        private val world: ServerWorld?,
        override val capacity: Int
) : IStorageDisk<ItemStack> {
    companion object {
        const val NBT_VERSION = "Version"
        const val NBT_CAPACITY = "Capacity"
        const val NBT_ITEMS = "Items"
        const val VERSION = 1
    }

    val rawStacks: Multimap<Item, ItemStack> = ArrayListMultimap.create()

    private var listener: IStorageDiskListener? = null
    private var context: IStorageDiskContainerContext? = null
    override fun writeToNbt(): CompoundTag? {
        val tag = CompoundTag()
        val list = ListTag()
        for (stack in rawStacks.values()) {
            list.add(serializeStackToNbt(stack!!))
        }
        tag.putInt(NBT_VERSION, VERSION)
        tag.put(NBT_ITEMS, list)
        tag.putInt(NBT_CAPACITY, capacity)
        return tag
    }

    override val factoryId: Identifier
        get() = ItemStorageDiskFactory.ID

    override fun getStacks(): Collection<ItemStack> {
        return rawStacks.values()
    }

    override fun insert(stack: ItemStack, size: Int, action: Action): ItemStack {
        if (stack.isEmpty) {
            return stack
        }
        for (otherStack in rawStacks[stack.item]) {
            if (instance().comparer.isEqualNoQuantity(otherStack, stack)) {
                return if (capacity != -1 && stored + size > capacity) {
                    val remainingSpace = capacity - stored
                    if (remainingSpace <= 0) {
                        return ItemStack(stack.item, size)
                    }

                    if (action == Action.PERFORM) {
                        otherStack.safeAdd(remainingSpace)
                        onChanged()
                    }

                    ItemStack(otherStack.item, size - remainingSpace)
                } else {
                    if (action == Action.PERFORM) {
                        otherStack.safeAdd(size)
                        onChanged()
                    }

                    ItemStack.EMPTY
                }
            }
        }

        return if (capacity != -1 && stored + size > capacity) {
            val remainingSpace = capacity - stored
            if (remainingSpace <= 0) {
                return ItemStack(stack.item, size)
            }
            if (action == Action.PERFORM) {
                rawStacks.put(stack.item, ItemStack(stack.item, remainingSpace))
                onChanged()
            }
            ItemStack(stack.item, size - remainingSpace)
        } else {
            if (action == Action.PERFORM) {
                rawStacks.put(stack.item, ItemStack(stack.item, size))
                onChanged()
            }
            ItemStack.EMPTY
        }
    }

    override fun extract(stack: ItemStack, size: Int, flags: Int, action: Action): ItemStack {
        var s = size
        if (stack.isEmpty) {
            return stack
        }
        for (otherStack in rawStacks[stack.item]) {
            if (instance().comparer.isEqual(otherStack, stack, flags)) {
                if (s > otherStack.count) {
                    s = otherStack.count
                }
                if (action == Action.PERFORM) {
                    if (otherStack.count - s == 0) {
                        rawStacks.remove(otherStack.item, otherStack)
                    } else {
                        otherStack.safeSubtract(s)
                    }
                    onChanged()
                }
                return ItemStack(otherStack.item, s)
            }
        }
        return ItemStack.EMPTY
    }

    override fun getStored(): Int {
        return rawStacks.values().stream().mapToInt { obj: ItemStack -> obj.count }.sum()
    }

    override fun getPriority(): Int {
        return 0
    }

    override fun getAccessType(): AccessType {
        return context!!.accessType
    }

    override fun setSettings(listener: IStorageDiskListener?, context: IStorageDiskContainerContext) {
        this.listener = listener
        this.context = context
    }

    override fun getCacheDelta(storedPreInsertion: Int, size: Int, remainder: ItemStack?): Int {
        if (accessType == AccessType.INSERT) {
            return 0
        }
        return if (remainder == null) size else size - remainder.count
    }

    private fun onChanged() {
        if (listener != null) {
            listener!!.onChanged()
        }
        if (world != null) {
            instance().getStorageDiskManager(world).markForSaving()
        }
    }

}