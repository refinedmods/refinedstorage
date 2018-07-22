package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ItemHandlerFluid extends ItemHandlerBase {
    private FluidStack[] fluids;

    public ItemHandlerFluid(int size, @Nullable Consumer<Integer> listener) {
        super(size, listener, s -> StackUtils.getFluid(ItemHandlerHelper.copyStackWithSize(s, 1), true).getValue() != null);

        this.fluids = new FluidStack[size];
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        ItemStack stack = getStackInSlot(slot);

        if (stack.isEmpty()) {
            setFluidStack(slot, null);
        } else {
            setFluidStack(slot, StackUtils.getFluid(ItemHandlerHelper.copyStackWithSize(stack, 1), true).getValue());
        }
    }

    public void setFluidStack(int slot, @Nullable FluidStack stack) {
        fluids[slot] = stack;
    }

    @Nullable
    public FluidStack getFluidStackInSlot(int slot) {
        return fluids[slot];
    }

    public FluidStack[] getFluids() {
        return fluids;
    }
}
