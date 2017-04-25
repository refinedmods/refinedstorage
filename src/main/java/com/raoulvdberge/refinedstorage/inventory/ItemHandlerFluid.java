package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.RSUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ItemHandlerFluid extends ItemHandlerBase {
    private FluidStack[] fluids;

    public ItemHandlerFluid(int size, @Nullable Consumer<Integer> listener) {
        super(size, listener, s -> RSUtils.getFluidFromStack(ItemHandlerHelper.copyStackWithSize(s, 1), true).getValue() != null);

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

    @Nullable
    public FluidStack getFluidStackInSlot(int slot) {
        return fluids[slot];
    }

    public FluidStack[] getFluids() {
        return fluids;
    }
}
