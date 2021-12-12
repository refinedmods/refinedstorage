package com.refinedmods.refinedstorage.screen.grid.filtering;

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

import java.util.List;
import java.util.function.Predicate;

public class OrGridFilter implements Predicate<IGridStack> {
    private final List<Predicate<IGridStack>> orPartFilters;

    private OrGridFilter(List<Predicate<IGridStack>> orPartFilters) {
        this.orPartFilters = orPartFilters;
    }

    public static Predicate<IGridStack> of(List<Predicate<IGridStack>> filters) {
        if (filters.isEmpty()) {
            return t -> false;
        }
        if (filters.size() == 1) {
            return filters.get(0);
        }
        return new OrGridFilter(filters);
    }

    @Override
    public boolean test(IGridStack gridStack) {
        for (Predicate<IGridStack> part : orPartFilters) {
            if (part.test(gridStack)) {
                return true;
            }
        }

        return false;
    }
}
