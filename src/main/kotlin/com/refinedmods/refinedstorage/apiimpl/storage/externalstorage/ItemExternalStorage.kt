package com.refinedmods.refinedstorage.apiimpl.storage.externalstorage

import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.util.Action
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelperimport
import java.util.*
import java.util.function.Supplier

class ItemExternalStorage(context: IExternalStorageContext?, handlerSupplier: Supplier<IItemHandler>, connectedToInterface: Boolean) : IExternalStorage<ItemStack?> {
    private val context: IExternalStorageContext?
    private val handlerSupplier: Supplier<IItemHandler>
    val isConnectedToInterface: Boolean
    private val cache = ItemExternalStorageCache()
    override fun update(network: INetwork?) {
        if (accessType === AccessType.INSERT) {
            return
        }
        cache.update(network, handlerSupplier.get())
    }

    val capacity: Long
        get() {
            val handler: IItemHandler = handlerSupplier.get() ?: return 0
            var capacity: Long = 0
            for (i in 0 until handler.getSlots()) {
                capacity += handler.getSlotLimit(i)
            }
            return capacity
        }
    val stacks: Collection<Any>?
        get() {
            val handler: IItemHandler = handlerSupplier.get() ?: return emptyList()
            val stacks: MutableList<ItemStack> = ArrayList<ItemStack>()
            for (i in 0 until handler.getSlots()) {
                stacks.add(handler.getStackInSlot(i))
            }
            return stacks
        }

    @Nonnull
    override fun insert(@Nonnull stack: ItemStack, size: Int, action: Action?): ItemStack {
        if (stack.isEmpty()) {
            return stack
        }
        val handler: IItemHandler = handlerSupplier.get()
        return if (handler != null && context.acceptsItem(stack)) {
            ItemHandlerHelper.insertItem(handler, ItemHandlerHelper.copyStackWithSize(stack, size), action === Action.SIMULATE)
        } else ItemHandlerHelper.copyStackWithSize(stack, size)
    }

    @Nonnull
    override fun extract(@Nonnull stack: ItemStack, size: Int, flags: Int, action: Action?): ItemStack {
        if (stack.isEmpty()) {
            return stack
        }
        val handler: IItemHandler = handlerSupplier.get() ?: return ItemStack.EMPTY
        var remaining = size
        var received: ItemStack = ItemStack.EMPTY
        for (i in 0 until handler.getSlots()) {
            val slot: ItemStack = handler.getStackInSlot(i)
            if (!slot.isEmpty() && API.instance().getComparer().isEqual(slot, stack, flags)) {
                val got: ItemStack = handler.extractItem(i, remaining, action === Action.SIMULATE)
                if (!got.isEmpty()) {
                    if (received.isEmpty()) {
                        received = got.copy()
                    } else {
                        received.grow(got.getCount())
                    }
                    remaining -= got.getCount()
                    if (remaining == 0) {
                        break
                    }
                }
            }
        }
        return received
    }

    val stored: Int
        get() {
            val handler: IItemHandler = handlerSupplier.get() ?: return 0
            var size = 0
            for (i in 0 until handler.getSlots()) {
                size += handler.getStackInSlot(i).getCount()
            }
            return size
        }
    val priority: Int
        get() = context.getPriority()
    val accessType: AccessType?
        get() = context.getAccessType()

    override fun getCacheDelta(storedPreInsertion: Int, size: Int, @Nullable remainder: ItemStack): Int {
        if (accessType === AccessType.INSERT) {
            return 0
        }
        return if (remainder == null) size else size - remainder.getCount()
    }

    init {
        this.context = context
        this.handlerSupplier = handlerSupplier
        isConnectedToInterface = connectedToInterface
    }
}