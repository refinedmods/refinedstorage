package com.refinedmods.refinedstorage.api.autocrafting.task.interceptor;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IOutputInterceptor {
    ItemStack intercept(ItemStack stack);

    FluidStack intercept(FluidStack stack);
}
