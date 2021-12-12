package com.refinedmods.refinedstorage.api.storage.disk;

import com.refinedmods.refinedstorage.api.storage.StorageType;
import net.minecraft.world.item.ItemStack;

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
     * @param id   the id to set
     */
    void setId(ItemStack disk, UUID id);

    /**
     * @param disk the disk
     * @return true if the given disk has an id set, false otherwise
     */
    boolean isValid(ItemStack disk);

    /**
     * @param disk the disk
     * @return the capacity of the given disk
     */
    int getCapacity(ItemStack disk);

    /**
     * @return the storage type
     */
    StorageType getType();
}
