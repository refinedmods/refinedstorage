package com.raoulvdberge.refinedstorage.render;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BakedModelOverrideRegistry {
    public interface BakedModelOverrideFactory {
        IBakedModel create(IBakedModel base, Map<ResourceLocation, IBakedModel> registry);
    }

    private Map<ResourceLocation, BakedModelOverrideFactory> registry = new HashMap<>();

    public void add(ResourceLocation id, BakedModelOverrideFactory factory) {
        registry.put(id, factory);
    }

    @Nullable
    public BakedModelOverrideFactory get(ResourceLocation id) {
        return registry.get(id);
    }
}
