package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IStackList
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.IoUtil
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.IoUtil.insertIntoInventory
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.SerializationUtil
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.nodeimport.ProcessingState
import com.refinedmods.refinedstorage.extensions.LIST_TAG_TYPE
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import reborncore.common.fluid.container.FluidInstance

class ProcessingNode : Node {
    private val singleItemSetToReceive: IStackList<ItemStack> = API.instance().createItemStackList()
    private val singleFluidSetToReceive: IStackList<FluidInstance> = API.instance().createFluidInstanceList()
    private var singleItemSetToRequire: IStackList<ItemStack>? = null
    private var singleFluidSetToRequire: IStackList<FluidInstance>? = null
    private var itemsReceived: IStackList<ItemStack> = API.instance().createItemStackList()
    private var fluidsReceived: IStackList<FluidInstance> = API.instance().createFluidInstanceList()
    var state: ProcessingState? = null
        private set
    private var quantityFinished = 0

    constructor(pattern: ICraftingPattern, root: Boolean) : super(pattern, root) {
        initSetsToReceive()
    }

    constructor(network: INetwork, tag: CompoundTag) : super(network, tag) {
        itemsReceived = SerializationUtil.readItemStackList(tag.getList(NBT_ITEMS_RECEIVED, LIST_TAG_TYPE))
        fluidsReceived = SerializationUtil.readFluidInstanceList(tag.getList(NBT_FLUIDS_RECEIVED, LIST_TAG_TYPE))
        singleItemSetToRequire = SerializationUtil.readItemStackList(tag.getList(NBT_SINGLE_ITEM_SET_TO_REQUIRE, LIST_TAG_TYPE))
        singleFluidSetToRequire = SerializationUtil.readFluidInstanceList(tag.getList(NBT_SINGLE_FLUID_SET_TO_REQUIRE, LIST_TAG_TYPE))
        state = ProcessingState.values()[tag.getInt(NBT_STATE)]
        initSetsToReceive()
    }

    private fun initSetsToReceive() {
        for (output in pattern.getOutputs()) {
            singleItemSetToReceive.add(output, output.count)
        }
        for (output in pattern.getFluidOutputs()) {
            singleFluidSetToReceive.add(output, output.amount.rawValue)
        }
    }

    override fun update(network: INetwork, ticks: Int, nodes: NodeList, internalStorage: IStorageDisk<ItemStack>, internalFluidStorage: IStorageDisk<FluidInstance>, listener: NodeListener) {
        if (state == ProcessingState.PROCESSED) {
            listener.onAllDone(this)
            return
        }
        if (quantity <= 0) {
            return
        }
        var allLocked = true
        var allMissingMachine = true
        var allRejected = true
        val originalState = state
        for (container in network.craftingManager.getAllContainers(pattern)) {
            val interval: Int = container.getUpdateInterval()
            if (interval < 0) {
                throw IllegalStateException("$container has an update interval of < 0")
            }
            if (interval == 0 || ticks % interval == 0) {
                for (i in 0 until container.getMaximumSuccessfulCraftingUpdates()) {
                    if (quantity <= 0) {
                        return
                    }
                    if (container.isLocked()) {
                        if (allLocked) {
                            state = ProcessingState.LOCKED
                        }
                        break
                    } else {
                        allLocked = false
                    }
                    if (!singleItemSetToReceive.isEmpty && container.getConnectedInventory() == null ||
                            !singleFluidSetToReceive.isEmpty && container.getConnectedFluidInventory() == null) {
                        if (allMissingMachine) {
                            state = ProcessingState.MACHINE_NONE
                        }
                        break
                    } else {
                        allMissingMachine = false
                    }
                    var hasAllRequirements = false
                    var extractedItems: IStackList<ItemStack> = IoUtil.extractFromInternalItemStorage(
                            requirements.getSingleItemRequirementSet(true), internalStorage, Action.SIMULATE
                    )
                    var extractedFluids: IStackList<FluidInstance>? = null
                    extractedFluids = IoUtil.extractFromInternalFluidStorage(requirements.getSingleFluidRequirementSet(true), internalFluidStorage, Action.SIMULATE)
                    var canInsertFullAmount = false
                    if (hasAllRequirements) {
                        canInsertFullAmount = IoUtil.insertIntoInventory(container.getConnectedInventory(), extractedItems, true).isEmpty()
                        if (canInsertFullAmount) {
                            canInsertFullAmount = IoUtil.insertIntoInventory(container.getConnectedFluidInventory(), extractedFluids.getStacks(), true)
                        }
                    }
                    if (hasAllRequirements && !canInsertFullAmount) {
                        if (allRejected) {
                            state = ProcessingState.MACHINE_DOES_NOT_ACCEPT
                        }
                        break
                    } else {
                        allRejected = false
                    }
                    if (hasAllRequirements && canInsertFullAmount) {
                        state = ProcessingState.READY
                        extractedItems = IoUtil.extractFromInternalItemStorage(requirements.getSingleItemRequirementSet(false), internalStorage, Action.PERFORM)
                        extractedFluids = IoUtil.extractFromInternalFluidStorage(requirements.getSingleFluidRequirementSet(false), internalFluidStorage, Action.PERFORM)
                        insertIntoInventory(container.getConnectedInventory(), extractedItems.getStacks(), Action.PERFORM)
                        insertIntoInventory(container.getConnectedFluidInventory(), extractedFluids.getStacks(), Action.PERFORM)
                        next()
                        listener.onSingleDone(this)
                        container.onUsedForProcessing()
                    }
                }
            }
        }
        if (originalState != state) {
            network.craftingManager.onTaskChanged()
        }
    }

    fun getSingleItemSetToReceive(): IStackList<ItemStack> {
        return singleItemSetToReceive
    }

    fun getSingleFluidSetToReceive(): IStackList<FluidInstance> {
        return singleFluidSetToReceive
    }

    fun getSingleItemSetToRequire(): IStackList<ItemStack>? {
        return singleItemSetToRequire
    }

    fun getSingleFluidSetToRequire(): IStackList<FluidInstance>? {
        return singleFluidSetToRequire
    }

    fun getNeeded(stack: ItemStack?): Int {
        return singleItemSetToReceive.getCount(stack) * totalQuantity - itemsReceived.getCount(stack)
    }

    fun getNeeded(stack: FluidInstance?): Int {
        return singleFluidSetToReceive.getCount(stack) * totalQuantity - fluidsReceived.getCount(stack)
    }

    val currentlyProcessing: Int
        get() {
            val unprocessed = totalQuantity - quantity
            return unprocessed - quantityFinished
        }

    fun markReceived(stack: ItemStack?, count: Int) {
        itemsReceived.add(stack, count)
        updateFinishedQuantity()
    }

    fun markReceived(stack: FluidInstance?, count: Int) {
        fluidsReceived.add(stack, count)
        updateFinishedQuantity()
    }

    fun updateFinishedQuantity() {
        var quantityFinished = totalQuantity
        for (toReceive in singleItemSetToReceive.getStacks()) {
            if (itemsReceived.get(toReceive.stack) != null) {
                val ratioReceived: Int = itemsReceived.get(toReceive.stack).getCount() / toReceive.stack.getCount()
                if (quantityFinished > ratioReceived) {
                    quantityFinished = ratioReceived
                }
            } else {
                quantityFinished = 0
            }
        }
        for (toReceive in singleFluidSetToReceive.getStacks()) {
            if (fluidsReceived.get(toReceive.stack) != null) {
                val ratioReceived: Int = fluidsReceived.get(toReceive.stack).getAmount() / toReceive.stack.getAmount()
                if (quantityFinished > ratioReceived) {
                    quantityFinished = ratioReceived
                }
            } else {
                quantityFinished = 0
            }
        }
        this.quantityFinished = quantityFinished
        if (this.quantityFinished == totalQuantity) {
            state = ProcessingState.PROCESSED
        }
    }

    override fun onCalculationFinished() {
        super.onCalculationFinished()
        singleItemSetToRequire = requirements.getSingleItemRequirementSet(true)
        singleFluidSetToRequire = requirements.getSingleFluidRequirementSet(true)
    }

    override fun writeToNbt(): CompoundTag? {
        val tag: CompoundTag? = super.writeToNbt()
        tag.put(NBT_ITEMS_RECEIVED, SerializationUtil.writeItemStackList(itemsReceived))
        tag.put(NBT_FLUIDS_RECEIVED, SerializationUtil.writeFluidInstanceList(fluidsReceived))
        tag.put(NBT_SINGLE_ITEM_SET_TO_REQUIRE, SerializationUtil.writeItemStackList(singleItemSetToRequire))
        tag.put(NBT_SINGLE_FLUID_SET_TO_REQUIRE, SerializationUtil.writeFluidInstanceList(singleFluidSetToRequire))
        tag.putInt(NBT_STATE, state!!.ordinal)
        return tag
    }

    companion object {
        private const val NBT_ITEMS_RECEIVED = "ItemsReceived"
        private const val NBT_FLUIDS_RECEIVED = "FluidsReceived"
        private const val NBT_SINGLE_ITEM_SET_TO_REQUIRE = "SingleItemSetToRequire"
        private const val NBT_SINGLE_FLUID_SET_TO_REQUIRE = "SingleFluidSetToRequire"
        private const val NBT_STATE = "State"
    }
}