package com.refinedmods.refinedstorage.api.storage.cache

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.storage.IStorage
import com.refinedmods.refinedstorage.api.util.IStackList


/**
 * This holds all stacks from all the connected storages from a [INetwork].
 *
 *
 * Refined Storage uses this class mainly for use in Grids and Detectors to avoid querying
 * individual [IStorage]s constantly (performance impact) and to send and detect storage changes
 * more efficiently.
 */
interface IStorageCache<T> {
    /**
     * Invalidates the cache.
     * Will also call [IStorageCache.sort] to sort the storages correctly.
     * Typically called when a [IStorageProvider] is added or removed from the network.
     *
     * @param cause the cause of the invalidate
     */
    fun invalidate(cause: InvalidateCause?)

    /**
     * Adds a stack to the cache.
     *
     *
     * Note that this doesn't modify any of the connected storages, but just modifies the cache.
     * Use [IStorage.insert] to add a stack to an actual storage.
     *
     *
     * Will merge it with another stack if it already exists.
     *
     * @param stack      the stack to add, do NOT modify
     * @param size       the size to add
     * @param rebuilding true if this method is called while rebuilding, false otherwise
     * @param batched    true if this change needs to be batched
     */
    fun add(stack: T, size: Int, rebuilding: Boolean, batched: Boolean)

    /**
     * Removes a stack from the cache.
     *
     *
     * Note that this doesn't modify any of the connected storages, but just modifies the cache.
     * Use [IStorage.extract] to remove a stack from an actual storage.
     *
     * @param stack   the stack to remove, do NOT modify
     * @param size    the size to remove
     * @param batched true if this change needs to be batched, false otherwise
     */
    fun remove(stack: T, size: Int, batched: Boolean)

    /**
     * Notifies storage cache listeners about batched up storage cache changes.
     */
    fun flush()

    /**
     * Adds a listener to be called when this storage cache changes.
     *
     * @param listener the listener
     */
    fun addListener(listener: IStorageCacheListener<T>)

    /**
     * Removes a listener from the storage cache.
     *
     * @param listener the listener
     */
    fun removeListener(listener: IStorageCacheListener<T>)

    /**
     * Re-attaches all listeners.
     * In practice this means that all listeners will get a [IStorageCacheListener.onAttached] call.
     */
    fun reAttachListeners()

    /**
     * Resorts the storages in this cache according to their priority.
     * This needs to be called when the priority of a storage changes.
     */
    fun sort()

    /**
     * @return the list behind this cache
     */
    fun getList(): IStackList<T>

    /**
     * @return a list of craftables
     */
    fun getCraftablesList(): IStackList<T>

    /**
     * @return the storages connected to this network
     */
    fun getStorages(): List<IStorage<T>>
}