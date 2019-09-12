package com.raoulvdberge.refinedstorage.inventory.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class FluidInventoryFilterIcon extends FluidInventory {
    public FluidInventoryFilterIcon(ItemStack stack) {
        super(1, Integer.MAX_VALUE, null);

        this.listener = slot -> {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundNBT());
            }

            // TODO ItemFilter.setFluidIcon(stack, getFluid(slot));
        };
/*
        FluidStack icon = ItemFilter.getFluidIcon(stack);
        if (icon != null) {
            setFluid(0, icon);
        }*/
    }
}
