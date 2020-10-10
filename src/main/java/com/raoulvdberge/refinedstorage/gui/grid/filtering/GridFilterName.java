package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.function.Predicate;

public class GridFilterName implements Predicate<IGridStack> {
    private String name;

    public GridFilterName(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public boolean test(IGridStack stack) {
        return stack.getName().toLowerCase().contains(name);
    }
}
