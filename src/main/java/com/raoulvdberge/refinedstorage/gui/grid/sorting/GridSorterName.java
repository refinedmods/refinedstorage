package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

public class GridSorterName implements IGridSorter {
    @Override
    public boolean isApplicable(IGrid grid) {
        return grid.getSortingType() == IGrid.SORTING_TYPE_NAME;
    }

    @Override
    public int compare(IGridStack left, IGridStack right, GridSorterDirection sortingDirection) {
        String leftName = left.getName();
        String rightName = right.getName();

        if (sortingDirection == GridSorterDirection.ASCENDING) {
            return leftName.compareTo(rightName);
        } else if (sortingDirection == GridSorterDirection.DESCENDING) {
            return rightName.compareTo(leftName);
        }

        return 0;
    }
}
