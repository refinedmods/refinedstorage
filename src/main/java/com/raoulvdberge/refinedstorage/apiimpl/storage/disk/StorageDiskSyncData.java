package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskSyncData;

public class StorageDiskSyncData implements IStorageDiskSyncData {
    private int stored;
    private int capacity;

    public StorageDiskSyncData(int stored, int capacity) {
        this.stored = stored;
        this.capacity = capacity;
    }

    @Override
    public int getStored() {
        return stored;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
