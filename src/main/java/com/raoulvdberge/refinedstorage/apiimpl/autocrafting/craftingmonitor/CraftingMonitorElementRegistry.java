package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementRegistry;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CraftingMonitorElementRegistry implements ICraftingMonitorElementRegistry {
    private Map<String, Function<ByteBuf, ICraftingMonitorElement>> registry = new HashMap<>();

    @Override
    public void add(String id, Function<ByteBuf, ICraftingMonitorElement> factory) {
        registry.put(id, factory);
    }

    @Nullable
    @Override
    public Function<ByteBuf, ICraftingMonitorElement> get(String id) {
        return registry.get(id);
    }
}
