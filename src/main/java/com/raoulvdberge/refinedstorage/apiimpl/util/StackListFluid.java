package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StackListFluid implements IStackList<FluidStack> {
    private ArrayListMultimap<Fluid, FluidStack> stacks = ArrayListMultimap.create();
    private List<FluidStack> removeTracker = new LinkedList<>();

    @Override
    @Nullable
    public void add(@Nonnull FluidStack stack, int size) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (stack.isFluidEqual(otherStack)) {
                otherStack.amount += size;

                return;
            }
        }

        stacks.put(stack.getFluid(), stack.copy());
    }

    @Override
    @Nullable
    public boolean remove(@Nonnull FluidStack stack, int size) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (stack.isFluidEqual(otherStack)) {
                otherStack.amount -= size;

                boolean success = otherStack.amount >= 0;

                if (otherStack.amount <= 0) {
                    stacks.remove(otherStack.getFluid(), otherStack);
                }

                return success;
            }
        }

        return false;
    }

    @Override
    public boolean trackedRemove(@Nonnull FluidStack stack, int size) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (stack.isFluidEqual(otherStack)) {
                FluidStack removed = new FluidStack(otherStack.getFluid(), Math.min(size, otherStack.amount));
                this.removeTracker.add(removed);
                otherStack.amount -= size;
                boolean success = otherStack.amount >= 0;

                if (otherStack.amount <= 0) {
                    stacks.remove(otherStack.getFluid(), otherStack);
                }

                return success;
            }
        }

        return false;
    }

    @Override
    public void undo() {
        removeTracker.forEach(s -> add(s, s.amount));
        removeTracker.clear();
    }

    @Override
    public List<FluidStack> getRemoveTracker() {
        return removeTracker;
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
    public void clean() {
        List<FluidStack> toRemove = stacks.values().stream()
            .filter(stack -> stack.amount <= 0)
            .collect(Collectors.toList());

        toRemove.forEach(stack -> stacks.remove(stack.getFluid(), stack));
    }

    @Override
    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    @Override
    public int getSizeFromStack(FluidStack stack) {
        return stack.amount;
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

    @Override
    public IStackList<FluidStack> getOredicted() {
        throw new UnsupportedOperationException("Fluid lists have no oredicted version!");
    }

    @Override
    public String toString() {
        return stacks.toString();
    }
}
