package com.raoulvdberge.refinedstorage.api.storage.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCache;

/**
 * An external storage handler.
 *
 * @param <T>
 */
public interface IStorageExternal<T> extends IStorage<T> {
    /**
     * For storage disks and blocks, the network detects changes and updates the {@link IStorageCache} accordingly.
     * However, for blocks connected to an external storage the external storage itself is responsible for bookkeeping the changes
     * and submitting them to the {@link IStorageCache}. That bookkeeping is supposed to happen in this method.
     * <p>
     * It's called every external storage tick.
     *
     * @param network the network
     */
    void update(INetwork network);

    /**
     * @return the capacity of the connected storage
     */
    int getCapacity();
}
