package com.refinedmods.refinedstorage.api.storage.tracker



/**
 * Represents a storage tracker entry.
 */
class StorageTrackerEntry(
    /**
     * the modification time
     */
    val time: Long,
    /**
     * the name of the player who modified this item
     */
    val name: String)