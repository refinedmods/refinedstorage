package com.refinedmods.refinedstorage.apiimpl.autocrafting.preview;

import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CraftingPreviewElementRegistry implements ICraftingPreviewElementRegistry {
    private Map<ResourceLocation, Function<PacketBuffer, ICraftingPreviewElement>> registry = new HashMap<>();

    @Override
    public void add(ResourceLocation id, Function<PacketBuffer, ICraftingPreviewElement> factory) {
        registry.put(id, factory);
    }

    @Nullable
    @Override
    public Function<PacketBuffer, ICraftingPreviewElement> get(ResourceLocation id) {
        return registry.get(id);
    }
}
