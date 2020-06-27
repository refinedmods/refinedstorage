package com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CraftingMonitorElementRegistry implements ICraftingMonitorElementRegistry {
    private final Map<ResourceLocation, Function<PacketBuffer, ICraftingMonitorElement>> registry = new HashMap<>();

    @Override
    public void add(ResourceLocation id, Function<PacketBuffer, ICraftingMonitorElement> factory) {
        registry.put(id, factory);
    }

    @Nullable
    @Override
    public Function<PacketBuffer, ICraftingMonitorElement> get(ResourceLocation id) {
        return registry.get(id);
    }
}
