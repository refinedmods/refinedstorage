package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.List;
import java.util.function.Predicate;

public class GridFilterAnd implements Predicate<IGridStack> {
    private final List<Predicate<IGridStack>> filters;

    private GridFilterAnd(List<Predicate<IGridStack>> filters) {
        this.filters = filters;
    }

    @Override
    public boolean test(IGridStack stack) {
        for (Predicate<IGridStack> filter : filters) {
            if (!filter.test(stack)) {
                return false;
            }
        }
        return true;
    }

    public static Predicate<IGridStack> of(List<Predicate<IGridStack>> filters) {
        if (filters.isEmpty()) {
            return t -> true;
        }
        if (filters.size() == 1) {
            return filters.get(0);
        }
        return new GridFilterAnd(filters);
    }
}
