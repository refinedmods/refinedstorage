package com.refinedmods.refinedstorage.screen.grid.sorting;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

public interface IGridSorter {
    boolean isApplicable(IGrid grid);

    int compare(IGridStack left, IGridStack right, SortingDirection direction);
}
