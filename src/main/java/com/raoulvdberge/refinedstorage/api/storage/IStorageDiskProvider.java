package com.raoulvdberge.refinedstorage.api.storage;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Implement this on an item that provides storage.
 */
public interface IStorageDiskProvider<T> {
    /**
     * @param disk the disk
     * @return the storage that this disk provides
     */
    @Nonnull
    IStorageDisk<T> create(ItemStack disk);
}
