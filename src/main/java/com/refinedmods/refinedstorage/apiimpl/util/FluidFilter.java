package com.refinedmods.refinedstorage.apiimpl.util;

import com.refinedmods.refinedstorage.api.util.IFilter;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidFilter implements IFilter {
    private final FluidStack stack;
    private final int compare;
    private final int mode;
    private final boolean modFilter;

    public FluidFilter(FluidStack stack, int compare, int mode, boolean modFilter) {
        this.stack = stack;
        this.compare = compare;
        this.mode = mode;
        this.modFilter = modFilter;
    }

    @Override
    public Object getStack() {
        return stack;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public boolean isModFilter() {
        return modFilter;
    }
}
