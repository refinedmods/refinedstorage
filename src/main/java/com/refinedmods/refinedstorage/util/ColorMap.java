package com.refinedmods.refinedstorage.util;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ColorMap<R, T extends R> {
    public static final DyeColor DEFAULT_COLOR = DyeColor.LIGHT_BLUE;

    protected final Map<DyeColor, DeferredHolder<R, T>> map = new EnumMap<>(DyeColor.class);
    private final Map<T, DyeColor> colorByBlock = new HashMap<>();
    protected final DeferredRegister<R> registry;
    protected List<Runnable> lateRegistration;

    public ColorMap(DeferredRegister<R> registry) {
        this.registry = registry;
    }

    public ColorMap(DeferredRegister<R> registry, List<Runnable> lateRegistration) {
        this.registry = registry;
        this.lateRegistration = lateRegistration;
    }

    public DeferredHolder<R, T> get(DyeColor color) {
        return map.get(color);
    }

    public DyeColor getColorFromObject(T object) {
        if (colorByBlock.isEmpty()) {
            map.forEach(((dyeColor, registryObject) -> colorByBlock.put(registryObject.get(), dyeColor)));
        }
        return colorByBlock.get(object);
    }

    public Collection<DeferredHolder<R, T>> values() {
        return map.values();
    }

    public void put(DyeColor color, DeferredHolder<R, T> object) {
        map.put(color, object);
    }

    public void forEach(BiConsumer<DyeColor, DeferredHolder<R, T>> consumer) {
        map.forEach(consumer);
    }
}
