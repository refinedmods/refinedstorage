package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

public class GridSortingQuantity extends GridSorting {
    @Override
    public int compare(IGridStack left, IGridStack right) {
        int leftSize = left.getQuantity();
        int rightSize = right.getQuantity();

        if (leftSize != rightSize) {
            if (sortingDirection == IGrid.SORTING_DIRECTION_ASCENDING) {
                return (leftSize > rightSize) ? 1 : -1;
            } else if (sortingDirection == IGrid.SORTING_DIRECTION_DESCENDING) {
                return (rightSize > leftSize) ? 1 : -1;
            }
        }

        return 0;
    }
}
