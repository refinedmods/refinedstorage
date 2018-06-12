package com.raoulvdberge.refinedstorage.api.storage.disk;

import javax.annotation.Nullable;

/**
 * Stores factories for reproducing storage disks from disk.
 */
public interface IStorageDiskRegistry {
    /**
     * Adds a factory.
     *
     * @param id      the id of this factory
     * @param factory the factory
     */
    void add(String id, IStorageDiskFactory factory);

    /**
     * Gets a factory.
     *
     * @param id the factory id
     * @return the factory, or null if no factory was found
     */
    @Nullable
    IStorageDiskFactory get(String id);
}
