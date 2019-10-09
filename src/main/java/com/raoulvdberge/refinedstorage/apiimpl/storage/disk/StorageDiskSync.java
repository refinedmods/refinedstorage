package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskSync;
import com.raoulvdberge.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.raoulvdberge.refinedstorage.network.disk.StorageDiskSizeRequestMessage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageDiskSync implements IStorageDiskSync {
    private static final int THROTTLE_MS = 500;

    private Map<UUID, StorageDiskSyncData> data = new HashMap<>();
    private Map<UUID, Long> syncTime = new HashMap<>();

    @Nullable
    @Override
    public StorageDiskSyncData getData(UUID id) {
        return data.get(id);
    }

    public void setData(UUID id, StorageDiskSyncData data) {
        this.data.put(id, data);
    }

    @Override
    public void sendRequest(UUID id) {
        long lastSync = syncTime.getOrDefault(id, 0L);

        if (System.currentTimeMillis() - lastSync > THROTTLE_MS) {
            RS.NETWORK_HANDLER.sendToServer(new StorageDiskSizeRequestMessage(id));

            syncTime.put(id, System.currentTimeMillis());
        }
    }
}
