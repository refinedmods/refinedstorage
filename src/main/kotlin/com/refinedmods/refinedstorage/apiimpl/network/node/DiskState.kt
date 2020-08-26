package com.refinedmods.refinedstorage.apiimpl.network.node



enum class DiskState {
    NONE, NORMAL, DISCONNECTED, NEAR_CAPACITY, FULL;

    companion object {
        const val DISK_NEAR_CAPACITY_THRESHOLD = 75
        operator fun get(stored: Int, capacity: Int): DiskState {
            return if (stored == capacity) {
                FULL
            } else if ((stored.toFloat() / capacity.toFloat() * 100f).toInt() >= DISK_NEAR_CAPACITY_THRESHOLD) {
                NEAR_CAPACITY
            } else {
                NORMAL
            }
        }
    }
}