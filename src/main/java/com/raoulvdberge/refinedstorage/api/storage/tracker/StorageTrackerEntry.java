package com.raoulvdberge.refinedstorage.api.storage.tracker;

/**
 * Represents a storage tracker entry.
 */
public class StorageTrackerEntry {
    private final long time;
    private final String name;

    public StorageTrackerEntry(long time, String name) {
        this.time = time;
        this.name = name;
    }

    /**
     * @return the modification time
     */
    public long getTime() {
        return time;
    }

    /**
     * @return the name of the player who modified this item
     */
    public String getName() {
        return name;
    }
}
