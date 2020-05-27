package com.refinedmods.refinedstorage.inventory.fluid;

import com.refinedmods.refinedstorage.item.FilterItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class FilterFluidInventory extends FluidInventory {
    public FilterFluidInventory(ItemStack stack) {
        super(27, Integer.MAX_VALUE);

        this.addListener((handler, slot, reading) -> {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundNBT());
            }

            stack.getTag().put(FilterItem.NBT_FLUID_FILTERS, writeToNbt());
        });

        if (stack.hasTag() && stack.getTag().contains(FilterItem.NBT_FLUID_FILTERS)) {
            readFromNbt(stack.getTag().getCompound(FilterItem.NBT_FLUID_FILTERS));
        }
    }

    public NonNullList<FluidStack> getFilteredFluids() {
        NonNullList<FluidStack> list = NonNullList.create();

        for (FluidStack fluid : this.getFluids()) {
            if (fluid != null) {
                list.add(fluid);
            }
        }

        return list;
    }
}
