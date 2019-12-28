package com.raoulvdberge.refinedstorage.api.storage.cache;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IStackList;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This holds all stacks from all the connected storages from a {@link INetwork}.
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
     * <p>
     * Note that this doesn't modify any of the connected storages, but just modifies the cache.
     * Use {@link IStorage#insert(T, int, com.raoulvdberge.refinedstorage.api.util.Action)} to add a stack to an actual storage.
     * <p>
     * Will merge it with another stack if it already exists.
     *
     * @param stack      the stack to add, do NOT modify
     * @param size       the size to add
     * @param rebuilding true if this method is called while rebuilding, false otherwise
     * @param batched    true if this change needs to be batched
     */
    void add(@Nonnull T stack, int size, boolean rebuilding, boolean batched);

    /**
     * Removes a stack from the cache.
     * <p>
     * Note that this doesn't modify any of the connected storages, but just modifies the cache.
     * Use {@link IStorage#extract(T, int, int, com.raoulvdberge.refinedstorage.api.util.Action)} to remove a stack from an actual storage.
     *
     * @param stack   the stack to remove, do NOT modify
     * @param size    the size to remove
     * @param batched true if this change needs to be batched, false otherwise
     */
    void remove(@Nonnull T stack, int size, boolean batched);

    /**
     * Notifies storage cache listeners about batched up storage cache changes.
     */
    void flush();

    /**
     * Adds a listener to be called when this storage cache changes.
     *
     * @param listener the listener
     */
    void addListener(IStorageCacheListener<T> listener);

    /**
     * Removes a listener from the storage cache.
     *
     * @param listener the listener
     */
    void removeListener(IStorageCacheListener<T> listener);

    /**
     * Re-attaches all listeners.
     * In practice this means that all listeners will get a {@link IStorageCacheListener#onAttached()} call.
     */
    void reAttachListeners();

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
     * @return a list of craftables
     */
    IStackList<T> getCraftablesList();

    /**
     * @return the storages connected to this network
     */
    List<IStorage<T>> getStorages();
}
