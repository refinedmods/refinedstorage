package com.refinedmods.refinedstorage.screen.grid.sorting

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack

class NameGridSorter : IGridSorter {
    override fun isApplicable(grid: IGrid?): Boolean {
        return grid!!.sortingType == IGrid.SORTING_TYPE_NAME
    }

    override fun compare(left: IGridStack, right: IGridStack, sortingDirection: SortingDirection): Int {
        val leftName = left.name
        val rightName = right.name
        if (sortingDirection == SortingDirection.ASCENDING) {
            return leftName!!.compareTo(rightName!!)
        } else if (sortingDirection == SortingDirection.DESCENDING) {
            return rightName!!.compareTo(leftName!!)
        }
        return 0
    }
}