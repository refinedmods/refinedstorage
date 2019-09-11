package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Predicate;

public class GridFilterFilter implements Predicate<IGridStack> {
    private List<IFilter> filters;

    public GridFilterFilter(List<IFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean test(IGridStack stack) {
        if (filters.isEmpty()) {
            return true;
        }

        int lastMode = IFilter.MODE_WHITELIST;

        for (IFilter filter : filters) {
            lastMode = filter.getMode();

            if (stack instanceof GridStackItem && filter.getStack() instanceof ItemStack) {
                ItemStack stackInFilter = (ItemStack) filter.getStack();

                if (filter.isModFilter()) {
                    String stackModId = stack.getModId();
                    String filterModId = stackInFilter.getItem().getCreatorModId(stackInFilter);

                    if (filterModId != null && filterModId.equals(stackModId)) {
                        return filter.getMode() == IFilter.MODE_WHITELIST;
                    }
                } else if (API.instance().getComparer().isEqual(((GridStackItem) stack).getStack(), stackInFilter, filter.getCompare())) {
                    return filter.getMode() == IFilter.MODE_WHITELIST;
                }
            } else if (stack instanceof GridStackFluid && filter.getStack() instanceof FluidStack) {
                FluidStack stackInFilter = (FluidStack) filter.getStack();

                if (filter.isModFilter()) {
                    String stackInFilterModId = /* TODO FluidRegistry.getModId(stackInFilter)*/"bla bla";

                    if (stackInFilterModId != null && stackInFilterModId.equalsIgnoreCase(stack.getModId())) {
                        return filter.getMode() == IFilter.MODE_WHITELIST;
                    }
                } else if (API.instance().getComparer().isEqual(((GridStackFluid) stack).getStack(), stackInFilter, filter.getCompare())) {
                    return filter.getMode() == IFilter.MODE_WHITELIST;
                }
            }
        }

        return lastMode != IFilter.MODE_WHITELIST;
    }
}
