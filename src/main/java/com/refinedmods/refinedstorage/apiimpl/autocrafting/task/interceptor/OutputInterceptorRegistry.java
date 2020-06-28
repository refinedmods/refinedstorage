package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.interceptor;

import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptorFactory;
import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptorRegistry;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class OutputInterceptorRegistry implements IOutputInterceptorRegistry {
    private final Map<ResourceLocation, IOutputInterceptorFactory> factoryMap = new HashMap<>();

    @Override
    public void add(ResourceLocation id, IOutputInterceptorFactory factory) {
        factoryMap.put(id, factory);
    }

    @Nullable
    @Override
    public IOutputInterceptorFactory get(ResourceLocation id) {
        return factoryMap.get(id);
    }
}
