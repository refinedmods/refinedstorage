package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.google.common.collect.ArrayListMultimap;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.api.util.StackListEntry;
import com.raoulvdberge.refinedstorage.api.util.StackListResult;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FluidStackList implements IStackList<FluidStack> {
    private final ArrayListMultimap<Fluid, StackListEntry<FluidStack>> stacks = ArrayListMultimap.create();
    private final Map<UUID, FluidStack> index = new HashMap<>();

    @Override
    public StackListResult<FluidStack> add(@Nonnull FluidStack stack, int size) {
        if (stack.isEmpty() || size <= 0) {
            throw new IllegalArgumentException("Cannot accept empty stack");
        }

        for (StackListEntry<FluidStack> entry : stacks.get(stack.getFluid())) {
            FluidStack otherStack = entry.getStack();

            if (stack.isFluidEqual(otherStack)) {
                if ((long) otherStack.getAmount() + (long) size > Integer.MAX_VALUE) {
                    otherStack.setAmount(Integer.MAX_VALUE);
                } else {
                    otherStack.grow(size);
                }

                return new StackListResult<>(otherStack, entry.getId(), size);
            }
        }

        FluidStack newStack = stack.copy();
        newStack.setAmount(size);

        StackListEntry<FluidStack> newEntry = new StackListEntry<>(newStack);

        stacks.put(newStack.getFluid(), newEntry);
        index.put(newEntry.getId(), newEntry.getStack());

        return new StackListResult<>(newStack, newEntry.getId(), size);
    }

    @Override
    public StackListResult<FluidStack> add(@Nonnull FluidStack stack) {
        return add(stack, stack.getAmount());
    }

    @Override
    public StackListResult<FluidStack> remove(@Nonnull FluidStack stack, int size) {
        for (StackListEntry<FluidStack> entry : stacks.get(stack.getFluid())) {
            FluidStack otherStack = entry.getStack();

            if (stack.isFluidEqual(otherStack)) {
                if (otherStack.getAmount() - size <= 0) {
                    stacks.remove(otherStack.getFluid(), entry);
                    index.remove(entry.getId());

                    return new StackListResult<>(otherStack, entry.getId(), -otherStack.getAmount());
                } else {
                    otherStack.shrink(size);

                    return new StackListResult<>(otherStack, entry.getId(), -size);
                }
            }
        }

        return null;
    }

    @Override
    public StackListResult<FluidStack> remove(@Nonnull FluidStack stack) {
        return remove(stack, stack.getAmount());
    }

    @Override
    @Nullable
    public FluidStack get(@Nonnull FluidStack stack, int flags) {
        for (StackListEntry<FluidStack> entry : stacks.get(stack.getFluid())) {
            FluidStack otherStack = entry.getStack();

            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public StackListEntry<FluidStack> getEntry(@Nonnull FluidStack stack, int flags) {
        for (StackListEntry<FluidStack> entry : stacks.get(stack.getFluid())) {
            FluidStack otherStack = entry.getStack();

            if (API.instance().getComparer().isEqual(otherStack, stack, flags)) {
                return entry;
            }
        }

        return null;
    }

    @Override
    @Nullable
    public FluidStack get(UUID id) {
        return index.get(id);
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
    public Collection<StackListEntry<FluidStack>> getStacks() {
        return stacks.values();
    }

    @Override
    @Nonnull
    public IStackList<FluidStack> copy() {
        FluidStackList list = new FluidStackList();

        for (StackListEntry<FluidStack> entry : stacks.values()) {
            list.stacks.put(entry.getStack().getFluid(), new StackListEntry<>(entry.getId(), entry.getStack().copy()));
            list.index.put(entry.getId(), entry.getStack());
        }

        return list;
    }
}
