package com.refinedmods.refinedstorage.api.storage.disk



/**
 * Contains synced info about a storage disk.
 */
class StorageDiskSyncData(private val stored: Int, private val capacity: Int) {
    /**
     * @return the amount stored
     */
    fun getStored(): Int {
        return stored
    }

    /**
     * @return the capacity
     */
    fun getCapacity(): Int {
        return capacity
    }
}