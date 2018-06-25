package com.raoulvdberge.refinedstorage.api.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;

/**
 * Stores relevant information about the container where the {@link IStorageDisk} is in.
 */
public interface IStorageDiskContainerContext {
    /**
     * @return the access type
     */
    AccessType getAccessType();
}
