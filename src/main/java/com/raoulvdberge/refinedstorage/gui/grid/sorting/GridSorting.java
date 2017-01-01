package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.Comparator;

public abstract class GridSorting implements Comparator<IGridStack> {
    protected int sortingDirection;

    public void setSortingDirection(int sortingDirection) {
        this.sortingDirection = sortingDirection;
    }
}
