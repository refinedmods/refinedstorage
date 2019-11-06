package com.raoulvdberge.refinedstorage.screen.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;

public interface IGridSorter {
    boolean isApplicable(IGrid grid);

    int compare(IGridStack left, IGridStack right, SortingDirection direction);
}
