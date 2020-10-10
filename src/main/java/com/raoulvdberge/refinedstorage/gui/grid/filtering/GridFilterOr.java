package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.List;
import java.util.function.Predicate;

public class GridFilterOr implements Predicate<IGridStack> {
    private List<Predicate<IGridStack>> orPartFilters;

    private GridFilterOr(List<Predicate<IGridStack>> orPartFilters) {
        this.orPartFilters = orPartFilters;
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

    public static Predicate<IGridStack> of(List<Predicate<IGridStack>> filters) {
        if (filters.isEmpty()) {
            return t -> false;
        }
        if (filters.size() == 1) {
            return filters.get(0);
        }
        return new GridFilterOr(filters);
    }
}
