package com.refinedmods.refinedstorage.api.storage.cache

import com.refinedmods.refinedstorage.api.util.StackListResult


/**
 * Listens for storage cache changes.
 *
 * @param <T> the type
</T> */
interface IStorageCacheListener<T> {
    /**
     * Called when this storage cache listener is attached to a storage cache.
     */
    fun onAttached()

    /**
     * Called when the cache invalidates.
     */
    fun onInvalidated()

    /**
     * Called when the storage cache changes.
     *
     * @param delta the delta
     */
    fun onChanged(delta: StackListResult<T>?)

    /**
     * Called when the storage cache changes.
     *
     * @param deltas a list of deltas
     */
    fun onChangedBulk(deltas: List<StackListResult<T>?>?)
}