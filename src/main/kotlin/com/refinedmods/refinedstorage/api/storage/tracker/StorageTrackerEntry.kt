package com.refinedmods.refinedstorage.api.storage.tracker



/**
 * Represents a storage tracker entry.
 */
class StorageTrackerEntry(private val time: Long, private val name: String) {
    /**
     * @return the modification time
     */
    fun getTime(): Long {
        return time
    }

    /**
     * @return the name of the player who modified this item
     */
    fun getName(): String {
        return name
    }
}