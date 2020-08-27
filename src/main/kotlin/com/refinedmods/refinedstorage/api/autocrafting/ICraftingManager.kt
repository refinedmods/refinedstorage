package com.refinedmods.refinedstorage.api.autocrafting

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.Text
import reborncore.common.fluid.container.FluidInstance
import java.util.*


/**
 * The crafting manager handles the storing, updating, adding and deleting of crafting tasks in a network.
 */
interface ICraftingManager {
    /**
     * @return the crafting tasks in this network, do NOT modify this
     */
    fun getTasks(): Collection<ICraftingTask>

    /**
     * Returns a crafting task by id.
     *
     * @param id the id
     * @return the task, or null if no task was found for the given id
     */
    fun getTask(id: UUID): ICraftingTask?

    /**
     * @return named crafting pattern containers
     */
    fun getNamedContainers(): Map<Text, List<Inventory>>

    /**
     * Starts a crafting task.
     *
     * @param task the task to start
     */
    fun start(task: ICraftingTask)

    /**
     * Cancels a crafting task.
     *
     * @param id the id of the task to cancel, or null to cancel all
     */
    fun cancel(id: UUID?)

    /**
     * Creates a crafting task for a given stack, but doesn't add it to the list.
     *
     * @param stack    the stack to craft
     * @param quantity the quantity to craft
     * @return the calculation result
     */
    fun create(stack: ItemStack, quantity: Int): ICalculationResult

    /**
     * Creates a crafting task for a given stack, but doesn't add it to the list.
     *
     * @param stack    the stack to craft
     * @param quantity the quantity to craft
     * @return the calculation result
     */
    fun create(stack: FluidInstance, quantity: Int): ICalculationResult

    /**
     * Schedules a crafting task if the task isn't scheduled yet.
     *
     * @param source the source
     * @param stack  the stack
     * @param amount the amount of items to request
     * @return the crafting task created, or null if no task is created
     */
    fun request(source: Any, stack: ItemStack, amount: Int): ICraftingTask?

    /**
     * Schedules a crafting task if the task isn't scheduled yet.
     *
     * @param source the source
     * @param stack  the stack
     * @param amount the mB of the fluid to request
     * @return the crafting task created, or null if no task is created
     */
    fun request(source: Any, stack: FluidInstance, amount: Int): ICraftingTask?

    /**
     * Tracks an incoming stack.
     *
     * @param stack the stack, can be empty
     */
    fun track(stack: ItemStack, size: Int): Int

    /**
     * Tracks an incoming stack.
     *
     * @param stack the stack, can be empty
     */
    fun track(stack: FluidInstance, size: Int): Int

    /**
     * @return the crafting patterns in this network
     */
    fun getPatterns(): List<ICraftingPattern>

    /**
     * Rebuilds the pattern list.
     */
    fun invalidate()

    /**
     * Return a crafting pattern from an item stack.
     *
     * @param pattern the stack to get a pattern for
     * @return the crafting pattern, or null if none is found
     */
    fun getPattern(pattern: ItemStack): ICraftingPattern?

    /**
     * Return a crafting pattern from a fluid stack.
     *
     * @param pattern the stack to get a pattern for
     * @return the crafting pattern, or null if none is found
     */
    fun getPattern(pattern: FluidInstance): ICraftingPattern?

    /**
     * Updates the tasks in this manager.
     */
    fun update()

    /**
     * @param tag the tag to read from
     */
    fun readFromNbt(tag: CompoundTag)

    /**
     * @param tag the tag to write to
     * @return the written tag
     */
    fun writeToNbt(tag: CompoundTag): CompoundTag

    /**
     * @param listener the listener
     */
    fun addListener(listener: ICraftingMonitorListener)

    /**
     * @param listener the listener
     */
    fun removeListener(listener: ICraftingMonitorListener)

    /**
     * Calls all [ICraftingMonitorListener]s.
     */
    fun onTaskChanged()

    /**
     * @param pattern pattern to look for
     * @return a set with all containers that have this pattern
     */
    fun getAllContainers(pattern: ICraftingPattern): Set<ICraftingPatternContainer>
}