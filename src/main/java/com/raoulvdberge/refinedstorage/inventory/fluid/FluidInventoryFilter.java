package com.raoulvdberge.refinedstorage.inventory.fluid;

import com.raoulvdberge.refinedstorage.item.ItemFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class FluidInventoryFilter extends FluidInventory {
    public FluidInventoryFilter(ItemStack stack) {
        super(27, Integer.MAX_VALUE, null);

        this.listener = slot -> {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new CompoundNBT());
            }

            stack.getTagCompound().put(ItemFilter.NBT_FLUID_FILTERS, writeToNbt());
        };

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(ItemFilter.NBT_FLUID_FILTERS)) {
            readFromNbt(stack.getTagCompound().getCompound(ItemFilter.NBT_FLUID_FILTERS));
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
