package com.raoulvdberge.refinedstorage.apiimpl.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class StorageDiskRegistry implements IStorageDiskRegistry {
    private Map<String, IStorageDiskFactory> factories = new HashMap<>();

    @Override
    public void add(String id, IStorageDiskFactory factory) {
        factories.put(id, factory);
    }

    @Override
    @Nullable
    public IStorageDiskFactory get(String id) {
        return factories.get(id);
    }
}
