package com.raoulvdberge.refinedstorage.api.storage;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IStackList;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This holds all stacks from all the connected storages from a {@link INetworkMaster}.
 * <p>
 * Refined Storage uses this class mainly for use in Grids and Detectors to avoid querying
 * individual {@link IStorage}s constantly (performance impact) and to send and detect storage changes
 * more efficiently.
 */
public interface IStorageCache<T> {
    /**
     * Invalidates the cache.
     * Will also call {@link IStorageCache#sort()} to sort the storages correctly.
     * Typically called when a {@link IStorageProvider} is added or removed from the network.
     */
    void invalidate();

    /**
     * Adds a stack to the cache.
     * Will merge it with another stack if it already exists.
     *
     * @param stack      the stack to add, do NOT modify
     * @param size       the size to add
     * @param rebuilding true if this method is called while rebuilding, false otherwise
     */
    void add(@Nonnull T stack, int size, boolean rebuilding);

    /**
     * Removes a stack from the cache.
     *
     * @param stack the stack to remove, do NOT modify
     * @param size  the size to remove
     */
    void remove(@Nonnull T stack, int size);

    /**
     * Resorts the storages in this cache according to their priority.
     * This needs to be called when the priority of a storage changes.
     */
    void sort();

    /**
     * @return the list behind this cache
     */
    IStackList<T> getList();

    /**
     * @return the storages in this cache, do NOT modify
     */
    List<IStorage<T>> getStorages();

    /**
     * Adds a storage to the cache.
     * Will not add duplicate storages, it checks this with Object#equals.
     *
     * @param storage the storage
     * @return true if the storage was added successfully, false otherwise
     */
    boolean addStorage(IStorage<T> storage);
}
