package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

public class GridSortingLastModified extends GridSorting {
    @Override
    public int compare(IGridStack left, IGridStack right) {
        long lt = left.getTrackerEntry() != null ? left.getTrackerEntry().getTime() : 0;
        long rt = right.getTrackerEntry() != null ? right.getTrackerEntry().getTime() : 0;

        if (lt != rt) {
            // For "last modified" the comparison is reversed
            if (sortingDirection == IGrid.SORTING_DIRECTION_DESCENDING) {
                return Long.compare(rt, lt);
            } else if (sortingDirection == IGrid.SORTING_DIRECTION_ASCENDING) {
                return Long.compare(lt, rt);
            }
        }

        return 0;
    }
}
