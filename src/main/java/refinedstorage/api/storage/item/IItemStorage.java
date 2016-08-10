package refinedstorage.api.storage.item;

import net.minecraft.item.ItemStack;
import refinedstorage.api.storage.IStorageProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a storage sink for the storage network.
 * Provide this through an {@link IStorageProvider}.
 */
public interface IItemStorage {
    /**
     * @return Items stored in this storage
     */
    List<ItemStack> getItems();

    /**
     * Inserts an item to the storage network.
     *
     * @param stack    The stack prototype to insert, do NOT modify
     * @param size     The amount of that prototype that has to be inserted
     * @param simulate If we are simulating
     * @return null if the insert was successful, or a {@link ItemStack} with the remainder
     */
    @Nullable
    ItemStack insertItem(@Nonnull ItemStack stack, int size, boolean simulate);

    /**
     * Extracts an item from the storage network.
     * <p>
     * If the stack we found in the system is smaller than the requested size, return the stack anyway.
     * For example: if this method is called for dirt (64x) while there is only dirt (32x), return the dirt (32x) anyway.
     *
     * @param stack A prototype of the stack to extract, do NOT modify
     * @param size  The amount of that prototype that has to be extracted
     * @param flags On what we are comparing to extract this item, see {@link CompareUtils}
     * @return null if we didn't extract anything, or an {@link ItemStack} with the result
     */
    @Nullable
    ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags);

    /**
     * @return The amount of items stored in this storage
     */
    int getStored();

    /**
     * @return The priority of this storage
     */
    int getPriority();
}
