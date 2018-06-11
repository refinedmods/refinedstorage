package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.List;
import java.util.function.Predicate;

public class GridFilterOr implements Predicate<IGridStack> {
    private List<List<Predicate<IGridStack>>> orPartFilters;

    public GridFilterOr(List<List<Predicate<IGridStack>>> orPartFilters) {
        this.orPartFilters = orPartFilters;
    }

    @Override
    public boolean test(IGridStack gridStack) {
        for (List<Predicate<IGridStack>> orPart : orPartFilters) {
            for (Predicate<IGridStack> part : orPart) {
                if (part.test(gridStack)) {
                    return true;
                }
            }
        }

        return false;
    }
}
