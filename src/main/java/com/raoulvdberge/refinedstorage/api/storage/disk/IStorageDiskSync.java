package com.raoulvdberge.refinedstorage.api.storage.disk;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Used to send requests to the server to get storage disk information ({@link IStorageDiskSyncData}).
 */
public interface IStorageDiskSync {
    /**
     * Gets disk data by disk id.
     * Can return null if there is no response yet.
     *
     * @param id the disk id
     * @return the disk data, or null if there was no response from the server yet
     */
    @Nullable
    IStorageDiskSyncData getData(UUID id);

    /**
     * Sends a request to the server to get information from a storage disk.
     * Can be called in a loop or in high frequency, the request is throttled.
     *
     * @param id the disk id
     */
    void sendRequest(UUID id);
}
