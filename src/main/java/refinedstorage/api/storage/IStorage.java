package refinedstorage.api.storage;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a storage sink for the storage network.
 * Provide this through an {@link IStorageProvider}.
 */
public interface IStorage {
    /**
     * @return Items stored in this storage
     */
    List<ItemStack> getItems();

    /**
     * Pushes an item to this storage.
     *
     * @param stack    The stack prototype to push, do NOT modify
     * @param size     The amount of that prototype that has to be pushed
     * @param simulate If we are simulating
     * @return null if the push was successful, or a {@link ItemStack} with the remainder
     */
    @Nullable
    ItemStack push(@Nonnull ItemStack stack, int size, boolean simulate);

    /**
     * Takes an item from storage.
     * If the stack we found in the system is smaller than the requested size, return the stack anyway.
     * For example: if this method is called for dirt (64x) while there is only dirt (32x), return the dirt (32x) anyway.
     *
     * @param stack A prototype of the stack to take, do NOT modify
     * @param size  The amount of that prototype that has to be taken
     * @param flags On what we are comparing to take the item, see {@link CompareFlags}
     * @return null if we didn't take anything, or a {@link ItemStack} with the result
     */
    @Nullable
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
