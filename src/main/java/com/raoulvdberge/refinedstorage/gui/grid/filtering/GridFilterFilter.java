package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.item.filter.Filter;
import com.raoulvdberge.refinedstorage.item.filter.ItemFilter;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class GridFilterFilter implements Predicate<IGridStack> {
    private List<Filter> filters;

    public GridFilterFilter(List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean test(IGridStack stack) {
        return accepts(filters, ((GridStackItem) stack).getStack(), stack.getModId());
    }

    public static boolean accepts(List<Filter> filters, ItemStack stack, String stackModId) {
        if (filters.isEmpty()) {
            return true;
        }

        int lastMode = ItemFilter.MODE_WHITELIST;

        for (Filter filter : filters) {
            lastMode = filter.getMode();

            if (filter.isModFilter()) {
                if (filter.getStack().getItem().getRegistryName().getResourceDomain().equalsIgnoreCase(stackModId)) {
                    return filter.getMode() == ItemFilter.MODE_WHITELIST;
                }
            } else if (API.instance().getComparer().isEqual(stack, filter.getStack(), filter.getCompare())) {
                return filter.getMode() == ItemFilter.MODE_WHITELIST;
            }
        }

        return lastMode != ItemFilter.MODE_WHITELIST;
    }
}
