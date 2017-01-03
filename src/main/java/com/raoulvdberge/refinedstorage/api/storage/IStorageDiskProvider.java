package com.raoulvdberge.refinedstorage.api.storage;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Implement this on an item.
 */
public interface IStorageDiskProvider<T> {
    @Nonnull
    IStorageDisk<T> create(ItemStack disk);
}
