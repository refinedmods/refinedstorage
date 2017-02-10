package com.raoulvdberge.refinedstorage.api.storage;

import net.minecraft.item.ItemStack;

/**
 * Represents a storage disk.
 *
 * @param <T>
 */
public interface IStorageDisk<T> extends IStorage<T> {
    /**
     * @return the capacity of this storage disk
     */
    int getCapacity();

    /**
     * @return whether this storage voids excess stacks
     */
    boolean isVoiding();

    /**
     * Returns whether the storage disk is valid.
     * Determines if it can be inserted in a disk drive.
     *
     * @param stack the disk
     * @return whether it's valid
     */
    boolean isValid(ItemStack stack);

    /**
     * Sets a listener that is called when the storage changes.
     *
     * @param listener the listener
     */
    void setListener(Runnable listener);

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
