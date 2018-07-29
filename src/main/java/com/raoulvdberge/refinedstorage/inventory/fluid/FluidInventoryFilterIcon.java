package com.raoulvdberge.refinedstorage.inventory.fluid;

import com.raoulvdberge.refinedstorage.item.ItemFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class FluidInventoryFilterIcon extends FluidInventory {
    public FluidInventoryFilterIcon(ItemStack stack) {
        super(1, Integer.MAX_VALUE, null);

        this.listener = slot -> {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            ItemFilter.setFluidIcon(stack, getFluid(slot));
        };

        FluidStack icon = ItemFilter.getFluidIcon(stack);
        if (icon != null) {
            setFluid(0, icon);
        }
    }
}
