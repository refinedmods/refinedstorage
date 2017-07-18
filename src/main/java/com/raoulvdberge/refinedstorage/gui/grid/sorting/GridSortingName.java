package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

public class GridSortingName extends GridSorting {
    @Override
    public int compare(IGridStack left, IGridStack right) {
        String leftName = left.getName();
        String rightName = right.getName();

        if (sortingDirection == IGrid.SORTING_DIRECTION_ASCENDING) {
            return leftName.compareTo(rightName);
        } else if (sortingDirection == IGrid.SORTING_DIRECTION_DESCENDING) {
            return rightName.compareTo(leftName);
        }

        return 0;
    }
}
