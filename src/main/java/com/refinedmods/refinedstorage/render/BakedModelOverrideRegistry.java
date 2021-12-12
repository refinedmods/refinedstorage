package com.refinedmods.refinedstorage.render;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BakedModelOverrideRegistry {
    private final Map<ResourceLocation, BakedModelOverrideFactory> registry = new HashMap<>();

    public void add(ResourceLocation id, BakedModelOverrideFactory factory) {
        registry.put(id, factory);
    }

    @Nullable
    public BakedModelOverrideFactory get(ResourceLocation id) {
        return registry.get(id);
    }

    public interface BakedModelOverrideFactory {
        BakedModel create(BakedModel base, Map<ResourceLocation, BakedModel> registry);
    }
}
