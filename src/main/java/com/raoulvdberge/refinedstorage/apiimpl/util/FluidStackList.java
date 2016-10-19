package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class FluidStackList implements IFluidStackList {
    private ArrayListMultimap<Fluid, FluidStack> stacks = ArrayListMultimap.create();

    @Override
    public void add(FluidStack stack) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (stack.isFluidEqual(otherStack)) {
                otherStack.amount += stack.amount;

                return;
            }
        }

        stacks.put(stack.getFluid(), stack.copy());
    }

    @Override
    public boolean remove(@Nonnull FluidStack stack, int size, boolean removeIfReachedZero) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (stack.isFluidEqual(otherStack)) {
                otherStack.amount -= size;

                if (otherStack.amount <= 0 && removeIfReachedZero) {
                    stacks.remove(otherStack.getFluid(), otherStack);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    @Nullable
    public FluidStack get(@Nonnull FluidStack stack, int flags) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Override
    @Nullable
    public FluidStack get(int hash) {
        for (FluidStack stack : this.stacks.values()) {
            if (API.instance().getFluidStackHashCode(stack) == hash) {
                return stack;
            }
        }

        return null;
    }

    @Override
    public void clear() {
        stacks.clear();
    }

    @Override
    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    @Nonnull
    @Override
    public Collection<FluidStack> getStacks() {
        return stacks.values();
    }

    @Override
    @Nonnull
    public IFluidStackList copy() {
        FluidStackList list = new FluidStackList();

        for (FluidStack stack : stacks.values()) {
            list.add(stack.copy());
        }

        return list;
    }

    @Override
    public String toString() {
        return stacks.toString();
    }
}
