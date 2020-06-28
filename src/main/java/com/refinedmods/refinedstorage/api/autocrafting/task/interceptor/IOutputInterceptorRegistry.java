package com.refinedmods.refinedstorage.api.autocrafting.task.interceptor;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IOutputInterceptorRegistry {
    void add(ResourceLocation id, IOutputInterceptorFactory factory);

    @Nullable
    IOutputInterceptorFactory get(ResourceLocation id);
}
