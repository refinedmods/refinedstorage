package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IStackList
import com.refinedmods.refinedstorage.api.util.StackListEntry
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.extensions.transferFrom
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import org.apache.logging.log4j.LogManager
import reborncore.api.items.InventoryUtils
import reborncore.common.fluid.FluidUtil
import reborncore.common.fluid.FluidValue
import reborncore.common.fluid.container.FluidInstance
import reborncore.common.util.Tank
import java.util.*

object IoUtil {
    private val LOGGER = LogManager.getLogger(IoUtil::class.java)
    private const val DEFAULT_EXTRACT_FLAGS: Int = com.refinedmods.refinedstorage.api.util.IComparer.COMPARE_NBT
    fun extractFromInternalItemStorage(list: IStackList<ItemStack>, storage: IStorageDisk<ItemStack>, action: Action): IStackList<ItemStack> {
        val extracted: IStackList<ItemStack> = API.instance().createItemStackList()
        for (entry in list.stacks) {
            val result: ItemStack = storage.extract(entry.stack, entry.stack.count, DEFAULT_EXTRACT_FLAGS, action)
            if (result.isEmpty || result.count != entry.stack.count) {
                check(!(action === Action.PERFORM)) { "The internal crafting inventory reported that ${entry.stack} was available but we got $result" }
                return API.instance().createItemStackList()
            }
            extracted.add(result)
        }
        return extracted
    }

    fun extractFromInternalFluidStorage(list: IStackList<FluidInstance>, storage: IStorageDisk<FluidInstance>, action: Action): IStackList<FluidInstance>? {
        val extracted: IStackList<FluidInstance> = API.instance().createFluidInstanceList()
        for (entry in list.stacks) {
            val result: FluidInstance = storage.extract(entry.stack, entry.stack.amount.rawValue, DEFAULT_EXTRACT_FLAGS, action)
            if (result.isEmpty || result.amount !== entry.stack.amount) {
                check(!(action === Action.PERFORM)) { "The internal crafting inventory reported that " + entry.stack + " was available but we got " + result }
                return null
            }
            extracted.add(result)
        }
        return extracted
    }

    fun insertIntoInventory(dest: Inventory, toInsert: Collection<StackListEntry<ItemStack>>, action: Action): List<ItemStack> {
        if (toInsert.isEmpty()) {
            return emptyList()
        }

        // Merge What we can first
        val remainders = mutableListOf<ItemStack>()
        toInsert.forEach {
            val remainder = InventoryUtils.insertItemStacked(dest, it.stack, action == Action.SIMULATE)
            if(!remainder.isEmpty) {
                remainders.add(remainder)
            }
        }

        return remainders
    }

    fun insertIntoInventory(dest: Tank, toInsert: Collection<StackListEntry<Tank>>, action: Action): Map<Tank, FluidValue> {
        val remainders = mutableMapOf<Tank, FluidValue>()
        toInsert.forEach {
            FluidUtil.transferFluid(it.stack, dest, it.stack.fluidAmount)
            val remainder = dest.transferFrom(it.stack, it.stack.fluidAmount, action == Action.SIMULATE)
            if (!remainder.isEmpty) {
                remainders[it.stack] = remainder
            }
        }

        return remainders
    }

    fun extractItemsFromNetwork(toExtractInitial: IStackList<ItemStack>, network: INetwork, internalStorage: IStorageDisk<ItemStack>) {
        if (toExtractInitial.isEmpty) {
            return
        }
        val toRemove: MutableList<ItemStack> = ArrayList()
        for (toExtract in toExtractInitial.stacks) {
            val result: ItemStack = network.extractItem(toExtract.stack, toExtract.stack.count, Action.PERFORM)
            if (!result.isEmpty) {
                internalStorage.insert(toExtract.stack, result.count, Action.PERFORM)
                toRemove.add(result)
            }
        }
        for (stack in toRemove) {
            toExtractInitial.remove(stack)
        }
        if (toRemove.isNotEmpty()) {
            network.craftingManager.onTaskChanged()
        }
    }

    fun extractFluidsFromNetwork(toExtractInitial: IStackList<FluidInstance>, network: INetwork, internalStorage: IStorageDisk<FluidInstance>) {
        if (toExtractInitial.isEmpty) {
            return
        }
        val toRemove: MutableList<FluidInstance> = ArrayList()
        for (toExtract in toExtractInitial.stacks) {
            val result: FluidInstance = network.extractFluid(toExtract.stack, toExtract.stack.amount.rawValue, Action.PERFORM)
            if (!result.isEmpty) {
                internalStorage.insert(toExtract.stack, result.amount.rawValue, Action.PERFORM)
                toRemove.add(result)
            }
        }
        for (stack in toRemove) {
            toExtractInitial.remove(stack)
        }
        if (toRemove.isNotEmpty()) {
            network.craftingManager.onTaskChanged()
        }
    }
}