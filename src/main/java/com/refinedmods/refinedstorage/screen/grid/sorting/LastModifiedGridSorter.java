package com.refinedmods.refinedstorage.screen.grid.sorting;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

public class LastModifiedGridSorter implements IGridSorter {
    @Override
    public boolean isApplicable(IGrid grid) {
        return grid.getSortingType() == IGrid.SORTING_TYPE_LAST_MODIFIED;
    }

    @Override
    public int compare(IGridStack left, IGridStack right, SortingDirection sortingDirection) {
        long lt = left.getTrackerEntry() != null ? left.getTrackerEntry().getTime() : 0;
        long rt = right.getTrackerEntry() != null ? right.getTrackerEntry().getTime() : 0;

        if (lt != rt) {
            // For "last modified" the comparison is reversed
            if (sortingDirection == SortingDirection.DESCENDING) {
                return Long.compare(rt, lt);
            } else if (sortingDirection == SortingDirection.ASCENDING) {
                return Long.compare(lt, rt);
            }
        }

        return 0;
    }
}
