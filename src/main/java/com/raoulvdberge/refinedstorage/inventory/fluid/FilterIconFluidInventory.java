package com.raoulvdberge.refinedstorage.inventory.fluid;

import com.raoulvdberge.refinedstorage.item.FilterItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

public class FilterIconFluidInventory extends FluidInventory {
    public FilterIconFluidInventory(ItemStack stack) {
        super(1, Integer.MAX_VALUE);

        this.addListener((handler, slot, reading) -> {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundNBT());
            }

            FilterItem.setFluidIcon(stack, getFluid(slot));
        });

        FluidStack icon = FilterItem.getFluidIcon(stack);
        if (icon != null) {
            setFluid(0, icon);
        }
    }
}
