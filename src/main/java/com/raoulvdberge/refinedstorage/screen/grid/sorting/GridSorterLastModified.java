package com.raoulvdberge.refinedstorage.screen.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;

public class GridSorterLastModified implements IGridSorter {
    @Override
    public boolean isApplicable(IGrid grid) {
        return grid.getSortingType() == IGrid.SORTING_TYPE_LAST_MODIFIED;
    }

    @Override
    public int compare(IGridStack left, IGridStack right, GridSorterDirection sortingDirection) {
        long lt = left.getTrackerEntry() != null ? left.getTrackerEntry().getTime() : 0;
        long rt = right.getTrackerEntry() != null ? right.getTrackerEntry().getTime() : 0;

        if (lt != rt) {
            // For "last modified" the comparison is reversed
            if (sortingDirection == GridSorterDirection.DESCENDING) {
                return Long.compare(rt, lt);
            } else if (sortingDirection == GridSorterDirection.ASCENDING) {
                return Long.compare(lt, rt);
            }
        }

        return 0;
    }
}
