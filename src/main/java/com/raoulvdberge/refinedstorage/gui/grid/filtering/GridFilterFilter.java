package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class GridFilterFilter implements Predicate<IGridStack> {
    private List<IFilter> filters;

    public GridFilterFilter(List<IFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean test(IGridStack stack) {
        return accepts(filters, ((GridStackItem) stack).getStack(), stack.getModId());
    }

    public static boolean accepts(List<IFilter> filters, ItemStack stack, String stackModId) {
        if (filters.isEmpty()) {
            return true;
        }

        int lastMode = IFilter.MODE_WHITELIST;

        for (IFilter filter : filters) {
            lastMode = filter.getMode();

            if (filter.isModFilter()) {
                if (filter.getStack().getItem().getRegistryName().getResourceDomain().equalsIgnoreCase(stackModId)) {
                    return filter.getMode() == IFilter.MODE_WHITELIST;
                }
            } else if (API.instance().getComparer().isEqual(stack, filter.getStack(), filter.getCompare())) {
                return filter.getMode() == IFilter.MODE_WHITELIST;
            }
        }

        return lastMode != IFilter.MODE_WHITELIST;
    }
}
