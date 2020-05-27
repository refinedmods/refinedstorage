package com.refinedmods.refinedstorage.screen.grid.sorting;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

public class NameGridSorter implements IGridSorter {
    @Override
    public boolean isApplicable(IGrid grid) {
        return grid.getSortingType() == IGrid.SORTING_TYPE_NAME;
    }

    @Override
    public int compare(IGridStack left, IGridStack right, SortingDirection sortingDirection) {
        String leftName = left.getName();
        String rightName = right.getName();

        if (sortingDirection == SortingDirection.ASCENDING) {
            return leftName.compareTo(rightName);
        } else if (sortingDirection == SortingDirection.DESCENDING) {
            return rightName.compareTo(leftName);
        }

        return 0;
    }
}
