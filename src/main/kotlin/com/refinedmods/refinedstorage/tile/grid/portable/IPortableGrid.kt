package com.refinedmods.refinedstorage.tile.grid.portable

import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.items.IItemHandlerModifiable

interface IPortableGrid {
    @get:Nullable
    val cache: IStorageCache<*>?
    val itemCache: IStorageCache<ItemStack?>?
        get() = cache as IStorageCache<ItemStack?>?
    val fluidCache: IStorageCache<Any?>?
        get() = cache

    @get:Nullable
    val storage: IStorageDisk<*>?
    val itemStorage: IStorageDisk<ItemStack?>?
        get() = storage as IStorageDisk<ItemStack?>?
    val fluidStorage: IStorageDisk<Any?>?
        get() = storage

    fun drainEnergy(energy: Int)
    val energy: Int
    val disk: BaseItemHandler
    val filter: IItemHandlerModifiable
    val itemStorageTracker: IStorageTracker<ItemStack?>
    val fluidStorageTracker: IStorageTracker<Any?>
    val isGridActive: Boolean
    val diskState: PortableGridDiskState
}