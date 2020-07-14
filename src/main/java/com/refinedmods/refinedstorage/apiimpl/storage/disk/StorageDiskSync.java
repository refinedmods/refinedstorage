package com.refinedmods.refinedstorage.apiimpl.storage.disk;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskSync;
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.refinedmods.refinedstorage.network.disk.StorageDiskSizeRequestMessage;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageDiskSync implements IStorageDiskSync {
    private static final int THROTTLE_MS = 500;

    private final Map<UUID, StorageDiskSyncData> data = new HashMap<>();
    private final Map<UUID, Long> syncTime = new HashMap<>();

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
