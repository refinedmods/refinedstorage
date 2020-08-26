package com.refinedmods.refinedstorage.screen.grid.sorting

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack

interface IGridSorter {
    fun isApplicable(grid: IGrid?): Boolean
    fun compare(left: IGridStack, right: IGridStack, direction: SortingDirection): Int
}