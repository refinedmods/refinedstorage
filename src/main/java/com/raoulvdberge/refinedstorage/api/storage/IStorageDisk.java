package com.raoulvdberge.refinedstorage.api.storage;

import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

/**
 * Represents a storage disk.
 *
 * @param <T> the storage
 */
public interface IStorageDisk<T> extends IStorage<T> {
    /**
     * @return the capacity of this storage disk
     */
    int getCapacity();

    /**
     * Returns whether the storage disk is valid.
     * Determines if it can be inserted in a disk drive.
     *
     * @param stack the disk
     * @return true if the disk is valid, false otherwise
     */
    boolean isValid(ItemStack stack);

    /**
     * When this storage disk is inserted into a storage disk container, it has to adjust to the container's settings
     * and use the following parameters instead.
     *
     * @param listener   the listener to be called when the storage changes
     * @param voidExcess a supplier whether this storage should void excess stacks
     * @param accessType the access type of the container
     */
    void onPassContainerContext(Runnable listener, Supplier<Boolean> voidExcess, Supplier<AccessType> accessType);

    /**
     * Reads the storage from NBT.
     */
    void readFromNBT();

    /**
     * Writes the storage to NBT.
     */
    void writeToNBT();

    /**
     * @return the storage type
     */
    StorageDiskType getType();
}
