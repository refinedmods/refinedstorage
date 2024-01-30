package com.refinedmods.refinedstorage.inventory.fluid;

import com.refinedmods.refinedstorage.item.FilterItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class ConfiguredFluidsInFilterItemHandler extends FluidInventory {
    public ConfiguredFluidsInFilterItemHandler(ItemStack stack) {
        super(27, Integer.MAX_VALUE);

        this.addListener((handler, slot, reading) -> {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundTag());
            }

            stack.getTag().put(FilterItem.NBT_FLUID_FILTERS, writeToNbt());
        });

        if (stack.hasTag() && stack.getTag().contains(FilterItem.NBT_FLUID_FILTERS)) {
            readFromNbt(stack.getTag().getCompound(FilterItem.NBT_FLUID_FILTERS));
        }
    }

    public NonNullList<FluidStack> getConfiguredFluids() {
        NonNullList<FluidStack> list = NonNullList.create();

        for (FluidStack fluid : this.getFluids()) {
            if (fluid != null) {
                list.add(fluid);
            }
        }

        return list;
    }
}
