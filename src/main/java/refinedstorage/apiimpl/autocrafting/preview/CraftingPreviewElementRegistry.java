package refinedstorage.apiimpl.autocrafting.preview;

import io.netty.buffer.ByteBuf;
import refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import refinedstorage.api.autocrafting.preview.ICraftingPreviewElementRegistry;

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
    public Function<ByteBuf, ICraftingPreviewElement> getFactory(String id) {
        return registry.get(id);
    }
}
