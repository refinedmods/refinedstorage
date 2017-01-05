package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.Arrays;
import java.util.function.Predicate;

public class GridFilterOreDict implements Predicate<IGridStack> {
    private String oreName;

    public GridFilterOreDict(String oreName) {
        this.oreName = oreName.toLowerCase();
    }

    @Override
    public boolean test(IGridStack stack) {
        return Arrays.stream(stack.getOreIds()).anyMatch(oreName -> oreName.toLowerCase().startsWith(this.oreName));
    }
}
