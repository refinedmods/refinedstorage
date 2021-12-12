package com.refinedmods.refinedstorage.apiimpl.autocrafting.task;

import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskFactory;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskRegistry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CraftingTaskRegistry implements ICraftingTaskRegistry {
    private final Map<ResourceLocation, ICraftingTaskFactory> registry = new HashMap<>();

    @Override
    public void add(ResourceLocation id, ICraftingTaskFactory factory) {
        registry.put(id, factory);
    }

    @Override
    @Nullable
    public ICraftingTaskFactory get(ResourceLocation id) {
        return registry.get(id);
    }
}
