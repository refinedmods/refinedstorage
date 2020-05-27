package com.refinedmods.refinedstorage.api.storage.disk;

/**
 * Listens to storage disk changes.
 */
public interface IStorageDiskListener {
    /**
     * Called when any change happens.
     */
    void onChanged();
}
