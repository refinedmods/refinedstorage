package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class CraftingPatternInputs {
    private final List<NonNullList<ItemStack>> inputs;
    private final List<NonNullList<FluidStack>> fluidInputs;

    public CraftingPatternInputs(List<NonNullList<ItemStack>> inputs, List<NonNullList<FluidStack>> fluidInputs) {
        this.inputs = inputs;
        this.fluidInputs = fluidInputs;
    }

    public List<NonNullList<ItemStack>> getInputs() {
        return inputs;
    }

    public List<NonNullList<FluidStack>> getFluidInputs() {
        return fluidInputs;
    }
}
