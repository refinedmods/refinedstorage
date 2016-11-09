package com.raoulvdberge.refinedstorage.api.storage.item;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This holds all items from all the connected storages from a {@link INetworkMaster}.
 * <p>
 * Refined Storage uses this class mainly for use in Grids and Detectors to avoid querying
 * individual {@link IItemStorage}s constantly (performance impact) and to send and detect storage changes
 * more efficiently.
 */
public interface IItemStorageCache {
    /**
     * Invalidates the cache.
     * Typically called when a {@link IItemStorageProvider} is added or removed from the network.
     */
    void invalidate();

    /**
     * Adds an item to the cache.
     * <p>
     * Note that this doesn't modify any of the connected storages, but just modifies the cache.
     * Use {@link INetworkMaster#insertItem(ItemStack, int, boolean)} to add an item to an actual storage.
     * <p>
     * Will merge it with another item if it already exists.
     *
     * @param stack      the stack to add, do NOT modify
     * @param size       the size to add
     * @param rebuilding true if this method is called while rebuilding, false otherwise
     */
    void add(@Nonnull ItemStack stack, int size, boolean rebuilding);

    /**
     * Removes an item from the cache.
     * <p>
     * Note that this doesn't modify any of the connected storages, but just modifies the cache.
     * Use {@link INetworkMaster#extractItem(ItemStack, int, int, boolean)} to remove an item from an actual storage.
     *
     * @param stack the item to remove, do NOT modify
     * @param size  the size to remove
     */
    void remove(@Nonnull ItemStack stack, int size);

    /**
     * @return the list behind this cache
     */
    IItemStackList getList();

    /**
     * @return the item storages connected to this network
     */
    List<IItemStorage> getStorages();
}
