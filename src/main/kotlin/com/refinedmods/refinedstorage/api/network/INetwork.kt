package com.refinedmods.refinedstorage.api.network

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager
import com.refinedmods.refinedstorage.api.network.security.ISecurityManager
import com.refinedmods.refinedstorage.api.storage.IStorage
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IComparer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import reborncore.common.fluid.container.FluidInstance
import team.reborn.energy.EnergyStorage
import java.util.function.Predicate


/**
 * Represents a network.
 */
interface INetwork {
    /**
     * @return the energy usage per tick of this network
     */
    val energyUsage: Int

//    /**
//     * @return the energy storage
//     */
//    val energyStorage: EnergyStorage

    /**
     * @return the network type
     */
    val type: NetworkType

    /**
     * @return true if this network is able to run (usually corresponds to the redstone configuration), false otherwise
     */
    fun canRun(): Boolean

    /**
     * Updates the network.
     */
    fun update()

    /**
     * Called when the network is removed.
     */
    fun onRemoved()

//    /**
//     * @return a graph of connected nodes to this network
//     */
//    val nodeGraph: INetworkNodeGraph
//
//    /**
//     * @return the [ISecurityManager] of this network
//     */
//    val securityManager: ISecurityManager
//
//    /**
//     * @return the [ICraftingManager] of this network
//     */
//    val craftingManager: ICraftingManager

//    /**
//     * @return the [IItemGridHandler] of this network
//     */
//    val itemGridHandler: IItemGridHandler
//
//    /**
//     * @return the [IFluidGridHandler] of this network
//     */
//    val fluidGridHandler: IFluidGridHandler

//    /**
//     * @return the [INetworkItemManager] of this network
//     */
//    val networkItemManager: INetworkItemManager

//    /**
//     * @return the [&lt;ItemStack&gt;][IStorageCache] of this network
//     */
//    val itemStorageCache: IStorageCache<ItemStack>

//    /**
//     * @return the [<] of this network
//     */
//    val fluidStorageCache: IStorageCache<FluidInstance>

    /**
     * Inserts an item in this network.
     *
     * @param stack  the stack prototype to insert, can be empty, do NOT modify
     * @param size   the amount of that prototype that has to be inserted
     * @param action the action
     * @return an empty stack if the insert was successful, or a stack with the remainder
     */
    fun insertItem(stack: ItemStack, size: Int, action: Action): ItemStack

    /**
     * Inserts an item and notifies the crafting manager of the incoming item.
     *
     * @param stack the stack prototype to insert, can be empty, do NOT modify
     * @param size  the amount of that prototype that has to be inserted
     * @return an empty stack if the insert was successful, or a stack with the remainder
     */
    fun insertItemTracked(stack: ItemStack, size: Int): ItemStack {
//        val remainder = craftingManager.track(stack, size)
//        return if (remainder == 0) {
//            ItemStack.EMPTY
//        } else insertItem(stack, remainder, Action.PERFORM)
        return ItemStack.EMPTY
    }

    /**
     * Extracts an item from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param flags  the flags to compare on, see [IComparer]
     * @param action the action
     * @param filter a filter for the storage
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    fun extractItem(stack: ItemStack, size: Int, flags: Int, action: Action, filter: Predicate<IStorage<ItemStack>>): ItemStack

    /**
     * Extracts an item from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param flags  the flags to compare on, see [IComparer]
     * @param action the action
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    fun extractItem(stack: ItemStack, size: Int, flags: Int, action: Action): ItemStack {
        return extractItem(stack, size, flags, action, Predicate { true })
    }

    /**
     * Extracts an item from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param action the action
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    fun extractItem(stack: ItemStack, size: Int, action: Action): ItemStack {
        return extractItem(stack, size, IComparer.COMPARE_NBT, action)
    }

    /**
     * Inserts a fluid in this network.
     *
     * @param stack  the stack prototype to insert, do NOT modify
     * @param size   the amount of that prototype that has to be inserted
     * @param action the action
     * @return an empty stack if the insert was successful, or a stack with the remainder
     */
    fun insertFluid(stack: FluidInstance, size: Int, action: Action): FluidInstance

    /**
     * Inserts a fluid and notifies the crafting manager of the incoming fluid.
     *
     * @param stack the stack prototype to insert, do NOT modify
     * @param size  the amount of that prototype that has to be inserted
     * @return an empty stack if the insert was successful, or a stack with the remainder
     */
    fun insertFluidTracked(stack: FluidInstance, size: Int): FluidInstance {
//        val remainder: Int = craftingManager.track(stack, size)
//        return if (remainder == 0) {
//            FluidInstance.EMPTY
//        } else insertFluid(stack, remainder, Action.PERFORM)
        return FluidInstance.EMPTY
    }

    /**
     * Extracts a fluid from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param flags  the flags to compare on, see [IComparer]
     * @param action the action
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    fun extractFluid(stack: FluidInstance, size: Int, flags: Int, action: Action, filter: Predicate<IStorage<FluidInstance>>): FluidInstance

    /**
     * Extracts a fluid from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param flags  the flags to compare on, see [IComparer]
     * @param action the action
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    fun extractFluid(stack: FluidInstance, size: Int, flags: Int, action: Action): FluidInstance {
        return extractFluid(stack, size, flags, action, Predicate { true })
    }

    /**
     * Extracts a fluid from this network.
     *
     * @param stack  the prototype of the stack to extract, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param action the action
     * @return an empty stack if nothing was extracted, or a stack with the result
     */
    fun extractFluid(stack: FluidInstance, size: Int, action: Action): FluidInstance {
        return extractFluid(stack, size, IComparer.COMPARE_NBT, action)
    }

//    /**
//     * @return the storage tracker for items
//     */
//    val itemStorageTracker: IStorageTracker<ItemStack>
//
//    /**
//     * @return the storage tracker for fluids
//     */
//    val fluidStorageTracker: IStorageTracker<*>

    /**
     * @return the world where this network is in
     */
    val world: World

    /**
     * @return the position of this network in the world
     */
    val position: BlockPos

    /**
     * @return a read network
     */
    fun readFromNbt(tag: CompoundTag): INetwork

    /**
     * @param tag the tag to write to
     * @return a written tag
     */
    fun writeToNbt(tag: CompoundTag): CompoundTag

    /**
     * Marks the network dirty.
     */
    fun markDirty()
}