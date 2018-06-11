package com.raoulvdberge.refinedstorage.api.storage.disk;

import net.minecraft.item.ItemStack;

import java.util.UUID;

/**
 * Maps disk items to an id.
 * Implement this on a disk item.
 */
public interface IStorageDiskProvider {
    /**
     * @param disk the disk
     * @return the id of the disk
     */
    UUID getId(ItemStack disk);

    /**
     * @param disk the disk
     * @return true if the given disk has an id set, false otherwise
     */
    boolean isValid(ItemStack disk);

    /**
     * @return the storage type
     */
    StorageDiskType getType();
}
