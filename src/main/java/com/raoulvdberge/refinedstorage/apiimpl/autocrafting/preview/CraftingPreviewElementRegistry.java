package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview;

import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CraftingPreviewElementRegistry implements ICraftingPreviewElementRegistry {
    private Map<String, Function<ByteBuf, ICraftingPreviewElement>> registry = new HashMap<>();

    @Override
    public void add(String id, Function<ByteBuf, ICraftingPreviewElement> factory) {
        registry.put(id, factory);
    }

    @Nullable
    @Override
    public Function<ByteBuf, ICraftingPreviewElement> get(String id) {
        return registry.get(id);
    }
}
