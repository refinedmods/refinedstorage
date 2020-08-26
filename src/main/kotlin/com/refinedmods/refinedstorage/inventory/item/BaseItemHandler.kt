package com.refinedmods.refinedstorage.inventory.item

import com.refinedmods.refinedstorage.inventory.listener.InventoryListener
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.items.ItemStackHandler
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate

open class BaseItemHandler(size: Int) : ItemStackHandler(size) {
    private val listeners: MutableList<InventoryListener<BaseItemHandler>> = ArrayList()
    private val validators: MutableList<Predicate<ItemStack>> = ArrayList()
    var isEmpty = true
        private set
    private var reading = false
    fun addValidator(validator: Predicate<ItemStack>): BaseItemHandler {
        validators.add(validator)
        return this
    }

    fun addListener(listener: InventoryListener<BaseItemHandler>): BaseItemHandler {
        listeners.add(listener)
        return this
    }

    @Nonnull
    open fun insertItem(slot: Int, @Nonnull stack: ItemStack, simulate: Boolean): ItemStack? {
        if (!validators.isEmpty()) {
            for (validator in validators) {
                if (validator.test(stack)) {
                    return super.insertItem(slot, stack, simulate)
                }
            }
            return stack
        }
        return super.insertItem(slot, stack, simulate)
    }

    protected open fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        onChanged(slot)
    }

    fun onChanged(slot: Int) {
        isEmpty = stacks.stream().allMatch({ obj: ItemStack -> obj.isEmpty })
        listeners.forEach(Consumer { l: InventoryListener<BaseItemHandler> -> l.onChanged(this, slot, reading) })
    }

    fun deserializeNBT(tag: CompoundTag?) {
        super.deserializeNBT(tag)
        isEmpty = stacks.stream().allMatch({ obj: ItemStack -> obj.isEmpty })
    }

    fun setReading(reading: Boolean) {
        this.reading = reading
    }
}