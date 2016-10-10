package refinedstorage.api.storage.item;

import net.minecraft.item.ItemStack;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.util.IItemStackList;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This holds all items from all the connected storages from a {@link INetworkMaster}.
 * <p>
 * Refined Storage uses this class mainly for use in Grids and Detectors to avoid querying
 * individual {@link IItemStorage} constantly (performance impact) and to send and detect storage changes
 * more efficiently.
 */
public interface IItemStorageCache {
    /**
     * Rebuilds the cache.
     * Typically called when a {@link IItemStorageProvider} is added or removed from the network.
     */
    void rebuild();

    /**
     * Adds an item to the cache.
     * <p>
     * Note that this doesn't modify any of the connected storages, but just modifies the cache.
     * Use {@link INetworkMaster#insertItem(ItemStack, int, boolean)} to add an item to an actual storage.
     * <p>
     * Will merge it with another item if it already exists.
     *
     * @param stack      the stack to add, do NOT modify
     * @param rebuilding true if this method is called while rebuilding, false otherwise
     */
    void add(@Nonnull ItemStack stack, boolean rebuilding);

    /**
     * Removes an item from the cache.
     * <p>
     * Note that this doesn't modify any of the connected storages, but just modifies the cache.
     * Use {@link INetworkMaster#extractItem(ItemStack, int, int)} to remove an item from an actual storage.
     *
     * @param stack the item to remove, do NOT modify
     */
    void remove(@Nonnull ItemStack stack);

    /**
     * @return the list behind this cope
     */
    IItemStackList getList();

    /**
     * @return the item storages connected to this network
     */
    List<IItemStorage> getStorages();
}
