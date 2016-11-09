package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IClientStack;

public class GridFilterName implements IGridFilter {
    private String name;

    public GridFilterName(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public boolean accepts(IClientStack stack) {
        return stack.getName().toLowerCase().contains(name);
    }

    @Override
    public boolean isStrong() {
        return false;
    }
}
