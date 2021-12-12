package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview;

import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CraftingPreviewElementRegistry implements ICraftingPreviewElementRegistry {
    private final Map<ResourceLocation, Function<FriendlyByteBuf, ICraftingPreviewElement>> registry = new HashMap<>();

    @Override
    public void add(ResourceLocation id, Function<FriendlyByteBuf, ICraftingPreviewElement> factory) {
        registry.put(id, factory);
    }

    @Nullable
    @Override
    public Function<FriendlyByteBuf, ICraftingPreviewElement> get(ResourceLocation id) {
        return registry.get(id);
    }
}
