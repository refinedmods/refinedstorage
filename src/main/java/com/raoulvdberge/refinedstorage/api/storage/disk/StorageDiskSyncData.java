package com.raoulvdberge.refinedstorage.api.storage.disk;

/**
 * Contains synced info about a storage disk.
 */
public class StorageDiskSyncData {
    private int stored;
    private int capacity;

    public StorageDiskSyncData(int stored, int capacity) {
        this.stored = stored;
        this.capacity = capacity;
    }

    /**
     * @return the amount stored
     */
    public int getStored() {
        return stored;
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }
}
