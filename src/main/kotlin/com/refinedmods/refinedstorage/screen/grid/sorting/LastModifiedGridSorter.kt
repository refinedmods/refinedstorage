package com.refinedmods.refinedstorage.screen.grid.sorting

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack

class LastModifiedGridSorter : IGridSorter {
    override fun isApplicable(grid: IGrid?): Boolean {
        return grid!!.sortingType == IGrid.SORTING_TYPE_LAST_MODIFIED
    }

    override fun compare(left: IGridStack, right: IGridStack, sortingDirection: SortingDirection): Int {
        val lt = if (left.trackerEntry != null) left.trackerEntry.getTime() else 0
        val rt = if (right.trackerEntry != null) right.trackerEntry.getTime() else 0
        if (lt != rt) {
            // For "last modified" the comparison is reversed
            if (sortingDirection == SortingDirection.DESCENDING) {
                return java.lang.Long.compare(rt, lt)
            } else if (sortingDirection == SortingDirection.ASCENDING) {
                return java.lang.Long.compare(lt, rt)
            }
        }
        return 0
    }
}