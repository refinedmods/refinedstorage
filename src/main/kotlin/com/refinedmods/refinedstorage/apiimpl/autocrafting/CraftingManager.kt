package com.refinedmods.refinedstorage.apiimpl.autocrafting

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.calculator.CalculationResult
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.Identifier
import net.minecraft.util.text.Text
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.items.IItemHandlerModifiable
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.function.Consumer


class CraftingManager(private val network: INetwork) : ICraftingManager {
    private val containerInventories: MutableMap<Text?, MutableList<IItemHandlerModifiable?>?> = LinkedHashMap<Text?, MutableList<IItemHandlerModifiable?>?>()
    private val patternToContainer: MutableMap<ICraftingPattern?, MutableSet<ICraftingPatternContainer>> = HashMap()
    private val patterns: MutableList<ICraftingPattern?> = ArrayList()
    private val tasks: MutableMap<UUID?, ICraftingTask?> = LinkedHashMap()
    private val tasksToAdd: MutableList<ICraftingTask?> = ArrayList()
    private val tasksToCancel: MutableList<UUID> = ArrayList()
    private var tasksToRead: ListTag? = null
    private val throttledRequesters: MutableMap<Any, Long> = HashMap()
    private val listeners: MutableSet<ICraftingMonitorListener?> = HashSet()
    override fun getTasks(): Collection<ICraftingTask?>? {
        return tasks.values
    }

    @Nullable
    override fun getTask(id: UUID?): ICraftingTask? {
        return tasks[id]
    }

    override fun getNamedContainers(): Map<Text?, MutableList<IItemHandlerModifiable?>?>? {
        return containerInventories
    }

    override fun start(@Nonnull task: ICraftingTask?) {
        task!!.start()
        tasksToAdd.add(task)
        network.markDirty()
    }

    override fun cancel(@Nullable id: UUID?) {
        if (id == null) {
            tasksToCancel.addAll(tasks.keys)
        } else {
            tasksToCancel.add(id)
        }
        network.markDirty()
    }

    override fun create(stack: ItemStack?, quantity: Int): ICalculationResult? {
        val pattern: ICraftingPattern = getPattern(stack) ?: return CalculationResult(CalculationResultType.NO_PATTERN)
        val factory = instance().getCraftingTaskRegistry()!![pattern.getCraftingTaskFactoryId()]
                ?: return CalculationResult(CalculationResultType.NO_PATTERN)
        return factory.create(network, instance().createCraftingRequestInfo(stack), quantity, pattern)
    }

    override fun create(stack: FluidInstance?, quantity: Int): ICalculationResult? {
        val pattern: ICraftingPattern = getPattern(stack) ?: return CalculationResult(CalculationResultType.NO_PATTERN)
        val factory = instance().getCraftingTaskRegistry()!![pattern.getCraftingTaskFactoryId()]
                ?: return CalculationResult(CalculationResultType.NO_PATTERN)
        return factory.create(network, instance().createCraftingRequestInfo(stack), quantity, pattern)
    }

    override fun update() {
        if (network.canRun()) {
            if (tasksToRead != null) {
                for (i in tasksToRead!!.indices) {
                    val taskTag = tasksToRead!!.getCompound(i)
                    val taskType = Identifier(taskTag.getString(NBT_TASK_TYPE))
                    val taskData = taskTag.getCompound(NBT_TASK_DATA)
                    val factory = instance().getCraftingTaskRegistry()!![taskType]
                    if (factory != null) {
                        try {
                            val task = factory.createFromNbt(network, taskData)
                            tasks[task!!.getId()] = task
                        } catch (e: CraftingTaskReadException) {
                            LOGGER.error("Could not deserialize crafting task", e)
                        }
                    }
                }
                tasksToRead = null
            }
            val changed = !tasksToCancel.isEmpty() || !tasksToAdd.isEmpty()
            for (idToCancel in tasksToCancel) {
                if (tasks.containsKey(idToCancel)) {
                    tasks[idToCancel]!!.onCancelled()
                    tasks.remove(idToCancel)
                }
            }
            tasksToCancel.clear()
            for (task in tasksToAdd) {
                tasks[task!!.getId()] = task
            }
            tasksToAdd.clear()
            var anyFinished = false
            val it: MutableIterator<Map.Entry<UUID?, ICraftingTask?>> = tasks.entries.iterator()
            while (it.hasNext()) {
                val task = it.next().value
                if (task!!.update()) {
                    anyFinished = true
                    it.remove()
                }
            }
            if (changed || anyFinished) {
                onTaskChanged()
            }
            if (!tasks.isEmpty()) {
                network.markDirty()
            }
        }
    }

    override fun readFromNbt(tag: CompoundTag?) {
        tasksToRead = tag!!.getList(NBT_TASKS, Constants.NBT.TAG_COMPOUND)
    }

    override fun writeToNbt(tag: CompoundTag?): CompoundTag? {
        val list = ListTag()
        for (task in tasks.values) {
            val taskTag = CompoundTag()
            taskTag.putString(NBT_TASK_TYPE, task!!.getPattern()!!.getCraftingTaskFactoryId().toString())
            taskTag.put(NBT_TASK_DATA, task.writeToNbt(CompoundTag()))
            list.add(taskTag)
        }
        tag!!.put(NBT_TASKS, list)
        return tag
    }

    override fun addListener(listener: ICraftingMonitorListener?) {
        listeners.add(listener)
        listener!!.onAttached()
    }

    override fun removeListener(listener: ICraftingMonitorListener?) {
        listeners.remove(listener)
    }

    override fun onTaskChanged() {
        listeners.forEach(Consumer { obj: ICraftingMonitorListener? -> obj!!.onChanged() })
    }

    @Nullable
    override fun request(source: Any?, stack: ItemStack?, amount: Int): ICraftingTask? {
        var amount = amount
        if (isThrottled(source)) {
            return null
        }
        for (task in getTasks()!!) {
            if (task!!.getRequested()!!.item != null) {
                if (instance().getComparer()!!.isEqualNoQuantity(task.getRequested()!!.item!!, stack!!)) {
                    amount -= task.getQuantity()
                }
            }
        }
        if (amount > 0) {
            val result: ICalculationResult = create(stack, amount)
            if (result.isOk()) {
                start(result.getTask())
            } else {
                throttle(source)
            }
        }
        return null
    }

    @Nullable
    override fun request(source: Any?, stack: FluidInstance?, amount: Int): ICraftingTask? {
        var amount = amount
        if (isThrottled(source)) {
            return null
        }
        for (task in getTasks()!!) {
            if (task!!.getRequested()!!.fluid != null) {
                if (instance().getComparer()!!.isEqual(task.getRequested()!!.fluid!!, stack, IComparer.COMPARE_NBT)) {
                    amount -= task.getQuantity()
                }
            }
        }
        if (amount > 0) {
            val result: ICalculationResult = create(stack, amount)
            if (result.isOk()) {
                start(result.getTask())
            } else {
                throttle(source)
            }
        }
        return null
    }

    private fun throttle(source: Any?) {
        if (source != null) {
            throttledRequesters[source] = System.currentTimeMillis()
        }
    }

    private fun isThrottled(source: Any?): Boolean {
        if (source == null) {
            return false
        }
        val throttledSince = throttledRequesters[source] ?: return false
        return System.currentTimeMillis() - throttledSince < THROTTLE_DELAY_MS
    }

    override fun track(@Nonnull stack: ItemStack?, size: Int): Int {
        var size = size
        if (stack!!.isEmpty) {
            return 0
        }
        for (task in tasks.values) {
            size = task.onTrackedInsert(stack, size)
            if (size == 0) {
                return 0
            }
        }
        return size
    }

    override fun track(@Nonnull stack: FluidInstance?, size: Int): Int {
        var size = size
        if (stack.isEmpty()) {
            return 0
        }
        for (task in tasks.values) {
            size = task.onTrackedInsert(stack, size)
            if (size == 0) {
                return 0
            }
        }
        return size
    }

    override fun getPatterns(): List<ICraftingPattern?>? {
        return patterns
    }

    override fun invalidate() {
        network.itemStorageCache!!.getCraftablesList()!!.clear()
        network.fluidStorageCache!!.getCraftablesList()!!.clear()
        patterns.clear()
        containerInventories.clear()
        patternToContainer.clear()
        val containers: MutableList<ICraftingPatternContainer> = ArrayList()
        for (node in network.nodeGraph!!.all()!!) {
            if (node is ICraftingPatternContainer && node.isActive) {
                containers.add(node as ICraftingPatternContainer)
            }
        }
        containers.sort(java.util.Comparator { a: ICraftingPatternContainer, b: ICraftingPatternContainer -> b.getPosition()!!.compareTo(a.getPosition()) })
        for (container in containers) {
            for (pattern in container.getPatterns()!!) {
                patterns.add(pattern)
                for (output in pattern!!.getOutputs()) {
                    network.itemStorageCache!!.getCraftablesList()!!.add(output)
                }
                for (output in pattern.getFluidOutputs()) {
                    network.fluidStorageCache!!.getCraftablesList()!!.add(output)
                }
                var list = patternToContainer[pattern]
                if (list == null) {
                    list = LinkedHashSet()
                }
                list.add(container)
                patternToContainer[pattern] = list
            }
            val handler: IItemHandlerModifiable? = container.getPatternInventory()
            if (handler != null) {
                containerInventories.computeIfAbsent(container.getName()) { k: Text? -> ArrayList<IItemHandlerModifiable?>() }!!.add(handler)
            }
        }
        network.itemStorageCache!!.reAttachListeners()
        network.fluidStorageCache!!.reAttachListeners()
    }

    override fun getAllContainers(pattern: ICraftingPattern?): Set<ICraftingPatternContainer?>? {
        return patternToContainer.getOrDefault(pattern, emptySet())
    }

    @Nullable
    override fun getPattern(pattern: ItemStack?): ICraftingPattern? {
        for (patternInList in patterns) {
            for (output in patternInList!!.getOutputs()) {
                if (instance().getComparer()!!.isEqualNoQuantity(output!!, pattern!!)) {
                    return patternInList
                }
            }
        }
        return null
    }

    @Nullable
    override fun getPattern(pattern: FluidInstance?): ICraftingPattern? {
        for (patternInList in patterns) {
            for (output in patternInList!!.getFluidOutputs()) {
                if (instance().getComparer().isEqual(output, pattern, IComparer.COMPARE_NBT)) {
                    return patternInList
                }
            }
        }
        return null
    }

    companion object {
        private const val THROTTLE_DELAY_MS = 3000
        private val LOGGER = LogManager.getLogger(CraftingManager::class.java)
        private const val NBT_TASKS = "Tasks"
        private const val NBT_TASK_TYPE = "Type"
        private const val NBT_TASK_DATA = "Task"
    }
}