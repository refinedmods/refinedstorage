package com.raoulvdberge.refinedstorage.api.storage.disk;

/**
 * Contains storage disk data.
 */
public interface IStorageDiskSyncData {
    /**
     * @return the amount stored
     */
    int getStored();

    /**
     * @return the capacity, or -1 if infinite capacity
     */
    int getCapacity();
}
