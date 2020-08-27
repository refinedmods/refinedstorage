package com.refinedmods.refinedstorage.api.storage.disk

import com.refinedmods.refinedstorage.api.storage.AccessType


/**
 * Stores relevant information about the container where the [IStorageDisk] is in.
 */
interface IStorageDiskContainerContext {
    /**
     * @return the access type
     */
    fun getAccessType(): AccessType?
}