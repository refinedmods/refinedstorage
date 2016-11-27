package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.RSUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemHandlerFluid extends ItemHandlerBasic {
    private FluidStack[] fluids;

    public ItemHandlerFluid(int size, TileEntity tile) {
        super(size, tile, s -> RSUtils.getFluidFromStack(ItemHandlerHelper.copyStackWithSize(s, 1), true).getValue() != null);

        this.fluids = new FluidStack[size];
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        ItemStack stack = getStackInSlot(slot);

        if (stack.isEmpty()) {
            fluids[slot] = null;
        } else {
            fluids[slot] = RSUtils.getFluidFromStack(ItemHandlerHelper.copyStackWithSize(stack, 1), true).getValue();
        }
    }

    public FluidStack getFluidStackInSlot(int slot) {
        return fluids[slot];
    }

    public FluidStack[] getFluids() {
        return fluids;
    }
}
