package com.raoulvdberge.refinedstorage.render.constants;

public final class ConstantsDisk {
    public static final int DISK_STATE_NORMAL = 0;
    public static final int DISK_STATE_NEAR_CAPACITY = 1;
    public static final int DISK_STATE_FULL = 2;
    public static final int DISK_STATE_DISCONNECTED = 3;
    public static final int DISK_STATE_NONE = 4;

    public static final int DISK_NEAR_CAPACITY_THRESHOLD = 75;

    public static int getDiskState(int stored, int capacity) {
        if (stored == capacity) {
            return DISK_STATE_FULL;
        } else if ((int) ((float) stored / (float) capacity * 100F) >= DISK_NEAR_CAPACITY_THRESHOLD) {
            return DISK_STATE_NEAR_CAPACITY;
        } else {
            return DISK_STATE_NORMAL;
        }
    }
}
