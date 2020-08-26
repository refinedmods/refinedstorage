package com.refinedmods.refinedstorage.container.transfer

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.SlotItemHandler
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

class TransferManager(container: Container) {
    private val fromToMap: MutableMap<IInventoryWrapper, MutableList<IInventoryWrapper>> = HashMap()
    private val container: Container

    @Nullable
    private var notFoundHandler: Function<Int, ItemStack>? = null
    fun clearTransfers() {
        fromToMap.clear()
    }

    fun setNotFoundHandler(@Nullable handler: Function<Int, ItemStack>?) {
        notFoundHandler = handler
    }

    fun addTransfer(from: IInventory, to: IItemHandler?) {
        addTransfer(InventoryInventoryWrapper(from), ItemHandlerInventoryWrapper(to))
    }

    fun addTransfer(from: IInventory, to: IInventory) {
        addTransfer(InventoryInventoryWrapper(from), InventoryInventoryWrapper(to))
    }

    fun addFilterTransfer(from: IInventory, itemTo: IItemHandlerModifiable, fluidTo: FluidInventory, typeGetter: Supplier<Int>) {
        addTransfer(InventoryInventoryWrapper(from), FilterInventoryWrapper(itemTo, fluidTo, typeGetter))
    }

    fun addItemFilterTransfer(from: IInventory, to: IItemHandlerModifiable) {
        addTransfer(InventoryInventoryWrapper(from), ItemFilterInventoryWrapper(to))
    }

    fun addFluidFilterTransfer(from: IInventory, to: FluidInventory) {
        addTransfer(InventoryInventoryWrapper(from), FluidFilterInventoryWrapper(to))
    }

    fun addTransfer(from: IItemHandler?, to: IInventory) {
        addTransfer(ItemHandlerInventoryWrapper(from), InventoryInventoryWrapper(to))
    }

    fun addBiTransfer(from: IInventory?, to: IItemHandler?) {
        addTransfer(from, to)
        addTransfer(to, from)
    }

    private fun addTransfer(from: IInventoryWrapper, to: IInventoryWrapper) {
        val toList = fromToMap.computeIfAbsent(from) { k: IInventoryWrapper? -> LinkedList() }
        toList.add(to)
    }

    fun transfer(index: Int): ItemStack {
        val slot: Slot = container.getSlot(index)
        val key: IInventoryWrapper
        key = if (slot is SlotItemHandler) {
            ItemHandlerInventoryWrapper((slot as SlotItemHandler).getItemHandler())
        } else {
            InventoryInventoryWrapper(slot.inventory)
        }
        val toList: List<IInventoryWrapper>? = fromToMap[key]
        if (toList != null) {
            val initial: ItemStack = slot.getStack().copy()
            var remainder: ItemStack = slot.getStack()
            for (to in toList) {
                val result = to.insert(remainder)
                if (result.type == InsertionResultType.STOP) {
                    break
                } else if (result.type == InsertionResultType.CONTINUE_IF_POSSIBLE) {
                    remainder = result.value
                    if (remainder.isEmpty) {
                        break
                    }
                }
            }
            slot.putStack(remainder)
            slot.onSlotChanged()
            if (instance().getComparer()!!.isEqual(remainder, initial) && notFoundHandler != null) {
                return notFoundHandler!!.apply(index)
            }
        } else if (notFoundHandler != null) {
            return notFoundHandler!!.apply(index)
        }
        return ItemStack.EMPTY
    }

    init {
        this.container = container
    }
}