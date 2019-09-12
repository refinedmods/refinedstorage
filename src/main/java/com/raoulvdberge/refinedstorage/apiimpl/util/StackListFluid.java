package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class StackListFluid implements IStackList<FluidStack> {
    private ArrayListMultimap<Fluid, FluidStack> stacks = ArrayListMultimap.create();

    @Override
    public void add(@Nonnull FluidStack stack, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Cannot accept empty stack");
        }

        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (stack.isFluidEqual(otherStack)) {
                if ((long) otherStack.getAmount() + (long) size > Integer.MAX_VALUE) {
                    otherStack.setAmount(Integer.MAX_VALUE);
                } else {
                    otherStack.grow(size);
                }

                return;
            }
        }

        FluidStack newStack = stack.copy();
        newStack.setAmount(size);
        stacks.put(stack.getFluid(), newStack);
    }

    @Override
    public void add(@Nonnull FluidStack stack) {
        add(stack, stack.getAmount());
    }

    @Override
    public boolean remove(@Nonnull FluidStack stack, int size) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (stack.isFluidEqual(otherStack)) {
                otherStack.shrink(size);

                boolean success = otherStack.getAmount() >= 0;

                if (otherStack.getAmount() <= 0) {
                    stacks.remove(otherStack.getFluid(), otherStack);
                }

                return success;
            }
        }

        return false;
    }

    @Override
    public boolean remove(@Nonnull FluidStack stack) {
        return remove(stack, stack.getAmount());
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
    public IStackList<FluidStack> copy() {
        StackListFluid list = new StackListFluid();

        for (FluidStack stack : stacks.values()) {
            list.stacks.put(stack.getFluid(), stack.copy());
        }

        return list;
    }
}
