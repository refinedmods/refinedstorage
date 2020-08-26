package com.refinedmods.refinedstorage.api.storage.disk



/**
 * Listens to storage disk changes.
 */
interface IStorageDiskListener {
    /**
     * Called when any change happens.
     */
    fun onChanged()
}