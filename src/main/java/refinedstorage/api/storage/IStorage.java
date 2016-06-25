package refinedstorage.api.storage;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Represents a storage sink for the storage network.
 * Provide this through an {@link IStorageProvider}.
 */
public interface IStorage {
    /**
     * Adds the items in this storage to the storage network.
     * This is called every 20 ticks or when the storage changes, so don't make this method too resource intensive.
     *
     * @param items A list of previously added items
     */
    void addItems(List<ItemStack> items);

    /**
     * Pushes an item to this storage.
     *
     * @param stack    The stack prototype to push, do NOT modify
     * @param size     The amount of that prototype that has to be pushed
     * @param simulate If we are simulating
     * @return null if the push was successful, or a {@link ItemStack} with the remainder
     */
    ItemStack push(@Nonnull ItemStack stack, int size, boolean simulate);

    /**
     * Takes an item from storage.
     * If the stack we found in the system is smaller than the requested size, return the stack anyway.
     * For example: this method is called for dirt (64x) while there is only dirt (32x), return the dirt (32x) anyway.
     *
     * @param stack A prototype of the stack to takeFromNetwork, do NOT modify
     * @param size  The amount of that prototype that has to be taken
     * @param flags On what we are comparing to takeFromNetwork the item, see {@link CompareFlags}
     * @return null if we didn't takeFromNetwork anything, or a {@link ItemStack} with the result
     */
    ItemStack take(@Nonnull ItemStack stack, int size, int flags);

    /**
     * @return The amount of items stored in this storage
     */
    int getStored();

    /**
     * @return The priority of this storage
     */
    int getPriority();
}
