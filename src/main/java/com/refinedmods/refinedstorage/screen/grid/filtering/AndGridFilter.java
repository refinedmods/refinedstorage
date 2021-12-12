package com.refinedmods.refinedstorage.screen.grid.filtering;

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

import java.util.List;
import java.util.function.Predicate;

public class AndGridFilter implements Predicate<IGridStack> {
    private final List<Predicate<IGridStack>> andPartFilters;

    private AndGridFilter(List<Predicate<IGridStack>> andPartFilters) {
        this.andPartFilters = andPartFilters;
    }

    public static Predicate<IGridStack> of(List<Predicate<IGridStack>> filters) {
        if (filters.isEmpty()) {
            return t -> true;
        }
        if (filters.size() == 1) {
            return filters.get(0);
        }
        return new AndGridFilter(filters);
    }

    @Override
    public boolean test(IGridStack gridStack) {
        for (Predicate<IGridStack> part : andPartFilters) {
            if (!part.test(gridStack)) {
                return false;
            }
        }

        return true;
    }
}
