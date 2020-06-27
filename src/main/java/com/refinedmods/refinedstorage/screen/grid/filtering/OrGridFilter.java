package com.refinedmods.refinedstorage.screen.grid.filtering;

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

import java.util.List;
import java.util.function.Predicate;

public class OrGridFilter implements Predicate<IGridStack> {
    private final List<List<Predicate<IGridStack>>> orPartFilters;

    public OrGridFilter(List<List<Predicate<IGridStack>>> orPartFilters) {
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
