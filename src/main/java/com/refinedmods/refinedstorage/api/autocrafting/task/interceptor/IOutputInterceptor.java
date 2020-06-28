package com.refinedmods.refinedstorage.api.autocrafting.task.interceptor;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public interface IOutputInterceptor {
    ItemStack intercept(MinecraftServer server, ItemStack stack);

    FluidStack intercept(MinecraftServer server, FluidStack stack);

    CompoundNBT writeToNbt(CompoundNBT tag);

    ResourceLocation getId();
}
