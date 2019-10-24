package com.raoulvdberge.refinedstorage.apiimpl.network.node;

public enum DiskState {
    NONE,
    NORMAL,
    DISCONNECTED,
    NEAR_CAPACITY,
    FULL;

    public static final int DISK_NEAR_CAPACITY_THRESHOLD = 75;

    public static DiskState get(int stored, int capacity) {
        if (stored == capacity) {
            return FULL;
        } else if ((int) ((float) stored / (float) capacity * 100F) >= DISK_NEAR_CAPACITY_THRESHOLD) {
            return NEAR_CAPACITY;
        } else {
            return NORMAL;
        }
    }
}
