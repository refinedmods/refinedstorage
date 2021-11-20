package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class CraftingPatternOutputs {
    private final NonNullList<ItemStack> outputs;
    private final NonNullList<ItemStack> byproducts;
    private final NonNullList<FluidStack> fluidOutputs;

    public CraftingPatternOutputs(NonNullList<ItemStack> outputs, NonNullList<ItemStack> byproducts, NonNullList<FluidStack> fluidOutputs) {
        this.outputs = outputs;
        this.byproducts = byproducts;
        this.fluidOutputs = fluidOutputs;
    }

    public NonNullList<ItemStack> getOutputs() {
        return outputs;
    }

    public NonNullList<ItemStack> getByproducts() {
        return byproducts;
    }

    public NonNullList<FluidStack> getFluidOutputs() {
        return fluidOutputs;
    }
}
