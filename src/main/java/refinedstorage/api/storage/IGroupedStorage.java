package refinedstorage.api.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.api.network.INetworkMaster;

import java.util.Collection;

/**
 * This holds all items from all the connected storages from a {@link INetworkMaster}.
 */
public interface IGroupedStorage {
    /**
     * Rebuilds the storages and items for a network. Typically called when a {@link IStorageProvider} is
     * added or removed from the network.
     */
    void rebuild();

    /**
     * Adds an item to the network. Will merge it with another item if it already exists.
     *
     * @param stack The stack to add, do NOT modify
     */
    void add(ItemStack stack);

    /**
     * Removes a item from the network.
     *
     * @param stack The item to remove, do NOT modify
     */
    void remove(ItemStack stack);

    /**
     * Gets an item from the network, does not decrement its count.
     *
     * @param stack The stack to find
     * @param flags The flags to compare on, see {@link CompareFlags}
     * @return The stack, do NOT modify
     */
    ItemStack get(ItemStack stack, int flags);

    /**
     * @return All stacks in this storage network
     */
    Collection<ItemStack> getStacks();
}
