package com.raoulvdberge.refinedstorage.api.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;

/**
 * Stores relevant information about the container where the {@link IStorageDisk} is in.
 */
public interface IStorageDiskContainerContext {
    /**
     * @return true if excess stacks can be voided, false otherwise
     */
    boolean isVoidExcess();

    /**
     * @return the access type
     */
    AccessType getAccessType();
}
