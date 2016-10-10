package refinedstorage.api.storage.item;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents an item storage sink for the storage network.
 * Provide this through an {@link IItemStorageProvider}.
 */
public interface IItemStorage {
    /**
     * @return items stored in this storage
     */
    List<ItemStack> getItems();

    /**
     * Inserts an item to this storage.
     *
     * @param stack    the stack prototype to insert, do NOT modify
     * @param size     the amount of that prototype that has to be inserted
     * @param simulate true if we are simulating, false otherwise
     * @return null if the insert was successful, or a stack with the remainder
     */
    @Nullable
    ItemStack insertItem(@Nonnull ItemStack stack, int size, boolean simulate);

    /**
     * Extracts an item from this storage.
     * <p>
     * If the stack we found in the system is smaller than the requested size, return that stack anyway.
     *
     * @param stack a prototype of the stack to extract, do NOT modify
     * @param size  the amount of that prototype that has to be extracted
     * @param flags the flags to compare on, see {@link refinedstorage.api.util.IComparer}
     * @return null if we didn't extract anything, or a stack with the result
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

    /**
     * READ(0) : Can see the items stored in this storage
     * WRITE(1) : Can insert and/or extract items from this storage
     * READ_WRITE(2) : Can see, insert and extract items from this storage
     *
     * @return the access type of this storage
     */
    int getAccessType();
}
