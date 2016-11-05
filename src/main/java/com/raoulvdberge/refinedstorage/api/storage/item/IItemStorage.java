package com.raoulvdberge.refinedstorage.api.storage.item;

import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an item storage sink for the storage network.
 * Provide this through an {@link IItemStorageProvider}.
 */
public interface IItemStorage extends IStorage<ItemStack> {
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
     * @param stack    a prototype of the stack to extract, do NOT modify
     * @param size     the amount of that prototype that has to be extracted
     * @param flags    the flags to compare on, see {@link IComparer}
     * @param simulate true if we are simulating, false otherwise
     * @return null if we didn't extract anything, or a stack with the result
     */
    @Nullable
    ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, boolean simulate);
}
