package com.refinedmods.refinedstorage.api.autocrafting.task.interceptor;

import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import net.minecraft.nbt.CompoundNBT;

/**
 * A factory that creates an output interceptor.
 * Register this factory in the {@link IOutputInterceptorRegistry}.
 */
public interface IOutputInterceptorFactory {
    IOutputInterceptor create(CompoundNBT tag) throws CraftingTaskReadException;
}
