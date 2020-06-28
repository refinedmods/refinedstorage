package com.refinedmods.refinedstorage.api.autocrafting.task;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ICraftingTaskOutputHook {
    ItemStack intercept(ItemStack stack);

    FluidStack intercept(FluidStack stack);
}
