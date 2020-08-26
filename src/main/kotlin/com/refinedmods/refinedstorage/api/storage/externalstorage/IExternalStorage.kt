package com.refinedmods.refinedstorage.api.storage.externalstorage

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.storage.IStorage
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache


/**
 * An external storage handler.
 *
 * @param <T>
</T> */
interface IExternalStorage<T> : IStorage<T> {
    /**
     * For storage disks and blocks, the network detects changes and updates the [IStorageCache] accordingly.
     * However, for blocks connected to an external storage the external storage itself is responsible for bookkeeping the changes
     * and submitting them to the [IStorageCache]. That bookkeeping is supposed to happen in this method.
     *
     *
     * It's called every external storage tick.
     *
     * @param network the network
     */
    fun update(network: INetwork?)

    /**
     * @return the capacity of the connected storage
     */
    fun getCapacity(): Long
}