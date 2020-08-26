package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.IStackList
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.monitor.CraftingMonitorElementFactory
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.Node
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.NodeList
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.ProcessingNode
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.NodeListener
import com.refinedmods.refinedstorage.apiimpl.storage.disk.FluidStorageDisk
import com.refinedmods.refinedstorage.apiimpl.storage.disk.ItemStorageDisk
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.FluidStorageDiskFactory
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.ItemStorageDiskFactory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import reborncore.common.fluid.container.FluidInstance
import java.util.*
import java.util.function.Consumer

class CraftingTask : ICraftingTask, NodeListener {
    private val internalStorage: IStorageDisk<ItemStack>
    private val internalFluidStorage: IStorageDisk<FluidInstance>
    private val network: INetwork
    private val requested: ICraftingRequestInfo
    override val quantity: Int
    private val pattern: ICraftingPattern?
    private val id: UUID
    private val nodes: NodeList
    private val toExtractInitial: IStackList<ItemStack>
    private val toExtractInitialFluids: IStackList<FluidInstance>
    private var ticks = 0
    override var startTime: Long = -1
    private var totalSteps = 0
    private var currentStep = 0
    private val craftingMonitorElementFactory: CraftingMonitorElementFactory = CraftingMonitorElementFactory()

    constructor(network: INetwork,
                requested: ICraftingRequestInfo,
                quantity: Int,
                pattern: ICraftingPattern?,
                nodes: NodeList,
                toExtractInitial: IStackList<ItemStack>,
                toExtractInitialFluids: IStackList<FluidInstance>) {
        this.network = network
        this.requested = requested
        this.quantity = quantity
        this.pattern = pattern
        id = UUID.randomUUID()
        this.nodes = nodes
        internalStorage = ItemStorageDisk(null, -1)
        internalFluidStorage = FluidStorageDisk(null, -1)
        this.toExtractInitial = toExtractInitial
        this.toExtractInitialFluids = toExtractInitialFluids
    }

    constructor(network: INetwork, tag: CompoundTag) {
        this.network = network
        requested = API.instance().createCraftingRequestInfo(tag.getCompound(NBT_REQUESTED))
        quantity = tag.getInt(NBT_QUANTITY)
        pattern = SerializationUtil.readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.world)
        id = tag.getUniqueId(NBT_ID)
        nodes = NodeList()
        ticks = tag.getInt(NBT_TICKS)
        startTime = tag.getLong(NBT_EXECUTION_STARTED)
        totalSteps = tag.getInt(NBT_TOTAL_STEPS)
        currentStep = tag.getInt(NBT_CURRENT_STEP)
        internalStorage = ItemStorageDiskFactory().createFromNbt(null, tag.getCompound(NBT_INTERNAL_STORAGE))
        internalFluidStorage = FluidStorageDiskFactory().createFromNbt(null, tag.getCompound(NBT_INTERNAL_FLUID_STORAGE))
        toExtractInitial = SerializationUtil.readItemStackList(tag.getList(NBT_TO_EXTRACT_INITIAL, Constants.NBT.TAG_COMPOUND))
        toExtractInitialFluids = SerializationUtil.readFluidInstanceList(tag.getList(NBT_TO_EXTRACT_INITIAL_FLUIDS, Constants.NBT.TAG_COMPOUND))
        val nodeList: ListTag = tag.getList(NBT_CRAFTS, Constants.NBT.TAG_COMPOUND)
        for (i in nodeList.indices) {
            val node: Node = Node.Companion.fromNbt(network, nodeList.getCompound(i))
            nodes.put(node.pattern, node)
        }
    }

    override fun writeToNbt(tag: CompoundTag): CompoundTag {
        tag.put(NBT_REQUESTED, requested.writeToNbt())
        tag.putInt(NBT_QUANTITY, quantity)
        tag.put(NBT_PATTERN, SerializationUtil.writePatternToNbt(pattern))
        tag.putInt(NBT_TICKS, ticks)
        tag.putUniqueId(NBT_ID, id)
        tag.putLong(NBT_EXECUTION_STARTED, startTime)
        tag.put(NBT_INTERNAL_STORAGE, internalStorage.writeToNbt())
        tag.put(NBT_INTERNAL_FLUID_STORAGE, internalFluidStorage.writeToNbt())
        tag.put(NBT_TO_EXTRACT_INITIAL, SerializationUtil.writeItemStackList(toExtractInitial))
        tag.put(NBT_TO_EXTRACT_INITIAL_FLUIDS, SerializationUtil.writeFluidInstanceList(toExtractInitialFluids))
        tag.putInt(NBT_TOTAL_STEPS, totalSteps)
        tag.putInt(NBT_CURRENT_STEP, currentStep)
        val nodeList = ListTag()
        for (node in nodes.all()) {
            nodeList.add(node!!.writeToNbt())
        }
        tag.put(NBT_CRAFTS, nodeList)
        return tag
    }

    override fun start() {
        nodes.all().forEach(Consumer { node: Node? ->
            totalSteps += node.quantity
            node!!.onCalculationFinished()
        })
        startTime = System.currentTimeMillis()
        IoUtil.extractItemsFromNetwork(toExtractInitial, network, internalStorage)
        IoUtil.extractFluidsFromNetwork(toExtractInitialFluids, network, internalFluidStorage)
    }

    override fun getCompletionPercentage(): Int =
            if (totalSteps == 0) { 0 }
            else {(currentStep.toFloat() * 100 / totalSteps).toInt()}

    override fun update(): Boolean {
        ++ticks
        return if (nodes.isEmpty) {
            val toPerform: MutableList<Runnable> = ArrayList()
            for (stack in internalStorage.getStacks()) {
                val remainder: ItemStack = network.insertItem(stack, stack.getCount(), Action.PERFORM)
                toPerform.add(Runnable { internalStorage.extract(stack, stack.getCount() - remainder.getCount(), IComparer.COMPARE_NBT, Action.PERFORM) })
            }
            for (stack in internalFluidStorage.getStacks()) {
                val remainder: FluidInstance = network.insertFluid(stack, stack.getAmount(), Action.PERFORM)
                toPerform.add(Runnable { internalFluidStorage.extract(stack, stack.getAmount() - remainder.getAmount(), IComparer.COMPARE_NBT, Action.PERFORM) })
            }

            // Prevent CME.
            toPerform.forEach(Consumer { obj: Runnable -> obj.run() })
            internalStorage.getStacks().isEmpty() && internalFluidStorage.getStacks().isEmpty()
        } else {
            IoUtil.extractItemsFromNetwork(toExtractInitial, network, internalStorage)
            IoUtil.extractFluidsFromNetwork(toExtractInitialFluids, network, internalFluidStorage)
            for (node in nodes.all()) {
                node!!.update(network, ticks, nodes, internalStorage, internalFluidStorage, this)
            }
            nodes.removeMarkedForRemoval()
            false
        }
    }

    override fun onCancelled() {
        nodes.unlockAll(network)
        for (remainder in internalStorage.getStacks()) {
            network.insertItem(remainder, remainder.getCount(), Action.PERFORM)
        }
        for (remainder in internalFluidStorage.getStacks()) {
            network.insertFluid(remainder, remainder.getAmount(), Action.PERFORM)
        }
    }

    override fun getRequested(): ICraftingRequestInfo? {
        return requested
    }

    override fun onTrackedInsert(stack: ItemStack, size: Int): Int {
        var size = size
        for (node in nodes.all()) {
            if (node is ProcessingNode) {
                val processing: ProcessingNode = node as ProcessingNode
                var needed: Int = processing.getNeeded(stack)
                if (needed > 0) {
                    if (needed > size) {
                        needed = size
                    }
                    processing.markReceived(stack, needed)
                    size -= needed
                    if (!processing.isRoot()) {
                        internalStorage.insert(stack, needed, Action.PERFORM)
                    } else {
                        val remainder: ItemStack = network.insertItem(stack, needed, Action.PERFORM)
                        internalStorage.insert(remainder, remainder.getCount(), Action.PERFORM)
                    }
                    network.craftingManager.onTaskChanged()
                    if (size == 0) {
                        return 0
                    }
                }
            }
        }
        return size
    }

    override fun onTrackedInsert(stack: FluidInstance, size: Int): Int {
        var size = size
        for (node in nodes.all()) {
            if (node is ProcessingNode) {
                val processing: ProcessingNode = node as ProcessingNode
                var needed: Int = processing.getNeeded(stack)
                if (needed > 0) {
                    if (needed > size) {
                        needed = size
                    }
                    processing.markReceived(stack, needed)
                    size -= needed
                    if (!processing.isRoot()) {
                        internalFluidStorage.insert(stack, needed, Action.PERFORM)
                    } else {
                        val remainder: FluidInstance = network.insertFluid(stack, needed, Action.PERFORM)
                        internalFluidStorage.insert(remainder, remainder.getAmount(), Action.PERFORM)
                    }
                    network.craftingManager.onTaskChanged()
                    if (size == 0) {
                        return 0
                    }
                }
            }
        }
        return size
    }

    override fun getCraftingMonitorElements(): List<ICraftingMonitorElement> {
        return craftingMonitorElementFactory.getElements(nodes.all(), internalStorage, internalFluidStorage)
    }
// TODO Is this initial get() or always get() ?
//    val craftingMonitorElements: List<Any?>?
//        get() = craftingMonitorElementFactory.getElements(nodes.all(), internalStorage, internalFluidStorage)

    override fun getPattern(): ICraftingPattern? {
        return pattern
    }

    override fun getId(): UUID {
        return id
    }

    override fun onAllDone(node: Node) {
        nodes.remove(node)
    }

    override fun onSingleDone(node: Node) {
        currentStep++
        network.craftingManager.onTaskChanged()
    }

    companion object {
        private const val NBT_REQUESTED = "Requested"
        private const val NBT_QUANTITY = "Quantity"
        private const val NBT_PATTERN = "Pattern"
        private const val NBT_TICKS = "Ticks"
        private const val NBT_ID = "Id"
        private const val NBT_EXECUTION_STARTED = "ExecutionStarted"
        private const val NBT_INTERNAL_STORAGE = "InternalStorage"
        private const val NBT_INTERNAL_FLUID_STORAGE = "InternalFluidStorage"
        private const val NBT_TO_EXTRACT_INITIAL = "ToExtractInitial"
        private const val NBT_TO_EXTRACT_INITIAL_FLUIDS = "ToExtractInitialFluids"
        private const val NBT_CRAFTS = "Crafts"
        private const val NBT_TOTAL_STEPS = "TotalSteps"
        private const val NBT_CURRENT_STEP = "CurrentStep"
    }
}