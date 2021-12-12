package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CraftingMonitorElementRegistry implements ICraftingMonitorElementRegistry {
    private final Map<ResourceLocation, Function<FriendlyByteBuf, ICraftingMonitorElement>> registry = new HashMap<>();

    @Override
    public void add(ResourceLocation id, Function<FriendlyByteBuf, ICraftingMonitorElement> factory) {
        registry.put(id, factory);
    }

    @Nullable
    @Override
    public Function<FriendlyByteBuf, ICraftingMonitorElement> get(ResourceLocation id) {
        return registry.get(id);
    }
}
