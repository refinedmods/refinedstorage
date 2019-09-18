package com.raoulvdberge.refinedstorage.render;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BakedModelOverrideRegistry {
    private Map<ResourceLocation, Function<IBakedModel, IBakedModel>> registry = new HashMap<>();

    public void add(ResourceLocation id, Function<IBakedModel, IBakedModel> factory) {
        registry.put(id, factory);
    }

    @Nullable
    public Function<IBakedModel, IBakedModel> get(ResourceLocation id) {
        return registry.get(id);
    }
}
