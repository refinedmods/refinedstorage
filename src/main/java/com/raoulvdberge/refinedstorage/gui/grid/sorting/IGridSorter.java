package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

public interface IGridSorter {
    boolean isApplicable(IGrid grid);

    int compare(IGridStack left, IGridStack right, GridSorterDirection direction);
}
