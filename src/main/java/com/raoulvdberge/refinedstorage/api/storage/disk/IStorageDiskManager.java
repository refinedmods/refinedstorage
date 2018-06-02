package com.raoulvdberge.refinedstorage.api.storage.disk;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Stores storage disks.
 */
public interface IStorageDiskManager {
    /**
     * Gets a storage disk by id.
     *
     * @param id the id
     * @return the storage disk, or null if no storage disk is found
     */
    @Nullable
    IStorageDisk get(UUID id);

    /**
     * Gets a storage disk by disk stack (a {@link IStorageDiskProvider}).
     *
     * @param disk the disk stack
     * @return the storage disk, or null if no storage disk is found
     */
    @Nullable
    IStorageDisk getByStack(ItemStack disk);

    /**
     * Sets a storage disk.
     *
     * @param id   the id
     * @param disk the disk
     */
    void set(UUID id, IStorageDisk disk);

    /**
     * Marks this manager for saving.
     */
    void markForSaving();
}
