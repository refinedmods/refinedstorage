package com.refinedmods.refinedstorage.api

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskRegistry
import com.refinedmods.refinedstorage.api.network.INetworkManager
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridBehavior
import com.refinedmods.refinedstorage.api.network.grid.IGridManager
import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeRegistry
import com.refinedmods.refinedstorage.api.storage.StorageType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskManager
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskRegistry
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskSync
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageProvider
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.IQuantityFormatter
import com.refinedmods.refinedstorage.api.util.IStackList
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import reborncore.common.fluid.container.FluidInstance


/**
 * Represents a Refined Storage API implementation.
 * Delivered by the [RSAPIInject] annotation.
 */
interface IRSAPI {
    /**
     * @return the comparer
     */
    fun getComparer(): IComparer?

    /**
     * @return the quantity formatter
     */
    fun getQuantityFormatter(): IQuantityFormatter?

    /**
     * @return the network node factory
     */
    fun getNetworkNodeRegistry(): INetworkNodeRegistry?

    /**
     * Gets a network node manager for a given world.
     *
     * @param world world
     * @return the network node manager for a given world
     */
    fun getNetworkNodeManager(world: ServerWorld?): INetworkNodeManager

    /**
     * Gets a network manager for a given world.
     *
     * @param world world
     * @return the network manager for a given world
     */
    fun getNetworkManager(world: ServerWorld?): INetworkManager?

    /**
     * @return the crafting task registry
     */
    fun getCraftingTaskRegistry(): ICraftingTaskRegistry?

    /**
     * @return the crafting monitor element registry
     */
    fun getCraftingMonitorElementRegistry(): ICraftingMonitorElementRegistry?

    /**
     * @return the crafting preview element registry
     */
    fun getCraftingPreviewElementRegistry(): ICraftingPreviewElementRegistry?

    /**
     * @return an empty item stack list
     */
    fun createItemStackList(): IStackList<ItemStack>

    /**
     * @return an empty fluid stack list
     */
    fun createFluidInstanceList(): IStackList<FluidInstance>

    /**
     * @return an empty crafting monitor element list
     */
    fun createCraftingMonitorElementList(): ICraftingMonitorElementList

    /**
     * @return the grid manager
     */
    fun getGridManager(): IGridManager?

    /**
     * @return the default crafting grid behavior
     */
    fun getCraftingGridBehavior(): ICraftingGridBehavior?

    /**
     * @return the storage disk registry
     */
    fun getStorageDiskRegistry(): IStorageDiskRegistry?

    /**
     * @param anyWorld any world associated with the server
     * @return the storage disk manager
     */
    fun getStorageDiskManager(anyWorld: ServerWorld?): IStorageDiskManager?

    /**
     * @return the storage disk sync manager
     */
    fun getStorageDiskSync(): IStorageDiskSync?

    /**
     * Adds an external storage provider for the given storage type.
     *
     * @param type     the storage type
     * @param provider the external storage provider
     */
    fun addExternalStorageProvider(type: StorageType?, provider: IExternalStorageProvider<*>?)

    /**
     * @param type the type
     * @return a set of external storage providers
     */
    fun getExternalStorageProviders(type: StorageType?): Set<IExternalStorageProvider<*>?>?

    /**
     * @param world    the world
     * @param capacity the capacity
     * @return a storage disk
     */
    fun createDefaultItemDisk(world: ServerWorld?, capacity: Int): IStorageDisk<ItemStack?>?

    /**
     * @param world    the world
     * @param capacity the capacity in mB
     * @return a fluid storage disk
     */
    fun createDefaultFluidDisk(world: ServerWorld?, capacity: Int): IStorageDisk<FluidInstance?>?

    /**
     * Creates crafting request info for an item.
     *
     * @param stack the stack
     * @return the request info
     */
    fun createCraftingRequestInfo(stack: ItemStack?): ICraftingRequestInfo?

    /**
     * Creates crafting request info for a fluid.
     *
     * @param stack the stack
     * @return the request info
     */
    fun createCraftingRequestInfo(stack: FluidInstance?): ICraftingRequestInfo?

    /**
     * Creates crafting request info from NBT.
     *
     * @param tag the nbt tag
     * @return the request info
     */
    @Throws(CraftingTaskReadException::class)
    fun createCraftingRequestInfo(tag: CompoundTag?): ICraftingRequestInfo?

    /**
     * @param renderHandler the render handler to add
     */
    fun addPatternRenderHandler(renderHandler: ICraftingPatternRenderHandler?)

    /**
     * @return a list of pattern render handlers
     */
    fun getPatternRenderHandlers(): List<ICraftingPatternRenderHandler?>?

    /**
     * @param stack the stack
     * @return a hashcode for the given stack
     */
    fun getItemStackHashCode(stack: ItemStack?): Int

    /**
     * @param stack the stack
     * @return a hashcode for the given stack
     */
    fun getFluidInstanceHashCode(stack: FluidInstance?): Int

    /**
     * @param node the node
     * @return the hashcode
     */
    fun getNetworkNodeHashCode(node: INetworkNode?): Int

    /**
     * @param left  the first network node
     * @param right the second network node
     * @return true if the two network nodes are equal, false otherwise
     */
    fun isNetworkNodeEqual(left: INetworkNode?, right: Any?): Boolean
}