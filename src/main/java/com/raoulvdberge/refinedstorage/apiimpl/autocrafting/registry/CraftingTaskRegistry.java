package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry;

import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CraftingTaskRegistry implements ICraftingTaskRegistry {
    private Map<String, ICraftingTaskFactory> registry = new HashMap<>();

    @Override
    public void add(String id, ICraftingTaskFactory factory) {
        registry.put(id, factory);
    }

    @Override
    @Nullable
    public ICraftingTaskFactory get(String id) {
        return registry.get(id);
    }
}
