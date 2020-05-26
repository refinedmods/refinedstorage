package com.refinedmods.refinedstorage.api.storage.cache;

public enum InvalidateCause {
    UNKNOWN,
    DISK_INVENTORY_CHANGED,
    CONNECTED_STATE_CHANGED,
    DEVICE_CONFIGURATION_CHANGED,
    INITIAL_TICK_INVALIDATION,
    NEIGHBOR_CHANGED
}
