package com.refinedmods.refinedstorage.api.autocrafting.task

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement
import com.refinedmods.refinedstorage.api.network.INetwork
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import reborncore.common.fluid.container.FluidInstance
import java.util.*


/**
 * Represents a crafting task.
 */
interface ICraftingTask {
    /**
     * Updates this task.
     *
     * @return true if this crafting task is finished, false otherwise
     */
    fun update(): Boolean

    /**
     * Called when this task is cancelled.
     */
    fun onCancelled()

    /**
     * @return the amount of items that have to be crafted
     */
    val quantity: Int

    /**
     * @return the completion percentage
     */
    fun getCompletionPercentage(): Int

    /**
     * @return the stack requested
     */
    fun getRequested(): ICraftingRequestInfo

    /**
     * Called when a stack is inserted into the system through [INetwork.insertItemTracked].
     *
     * @param stack the stack
     * @return the remainder of this stack after processing of the task
     */
    fun onTrackedInsert(stack: ItemStack, size: Int): Int

    /**
     * Called when a stack is inserted into the system through [INetwork.insertFluidTracked].
     *
     * @param stack the stack
     * @return the remainder of this stack after processing of the task
     */
    fun onTrackedInsert(stack: FluidInstance, size: Int): Int

    /**
     * Writes this task to NBT.
     *
     * @param tag the tag
     * @return the written tag
     */
    fun writeToNbt(tag: CompoundTag): CompoundTag

    /**
     * @return the elements of this task for display in the crafting monitor
     */
    fun getCraftingMonitorElements(): List<ICraftingMonitorElement>

    /**
     * @return the crafting pattern corresponding to this task
     */
    fun getPattern(): ICraftingPattern?

    /**
     * @return the unix time in ms when this task has started
     */
    var startTime: Long

    /**
     * @return the id of this task
     */
    fun getId(): UUID

    /**
     * Start the CraftingTask
     */
    fun start()
}