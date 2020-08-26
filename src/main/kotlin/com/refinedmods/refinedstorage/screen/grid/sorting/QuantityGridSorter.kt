package com.refinedmods.refinedstorage.screen.grid.sorting

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack

class QuantityGridSorter : IGridSorter {
    override fun isApplicable(grid: IGrid?): Boolean {
        return grid!!.sortingType == IGrid.SORTING_TYPE_QUANTITY
    }

    override fun compare(left: IGridStack, right: IGridStack, sortingDirection: SortingDirection): Int {
        val leftSize = left.quantity
        val rightSize = right.quantity
        if (leftSize != rightSize) {
            if (sortingDirection == SortingDirection.ASCENDING) {
                return if (leftSize > rightSize) 1 else -1
            } else if (sortingDirection == SortingDirection.DESCENDING) {
                return if (rightSize > leftSize) 1 else -1
            }
        }
        return 0
    }
}