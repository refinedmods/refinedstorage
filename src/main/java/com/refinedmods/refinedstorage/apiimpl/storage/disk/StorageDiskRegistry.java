package com.refinedmods.refinedstorage.apiimpl.storage.disk;

import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskRegistry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class StorageDiskRegistry implements IStorageDiskRegistry {
    private final Map<ResourceLocation, IStorageDiskFactory> factories = new HashMap<>();

    @Override
    public void add(ResourceLocation id, IStorageDiskFactory factory) {
        factories.put(id, factory);
    }

    @Override
    @Nullable
    public IStorageDiskFactory get(ResourceLocation id) {
        return factories.get(id);
    }
}
