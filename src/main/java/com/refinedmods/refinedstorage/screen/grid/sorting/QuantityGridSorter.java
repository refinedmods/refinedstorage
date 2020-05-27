package com.refinedmods.refinedstorage.screen.grid.sorting;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

public class QuantityGridSorter implements IGridSorter {
    @Override
    public boolean isApplicable(IGrid grid) {
        return grid.getSortingType() == IGrid.SORTING_TYPE_QUANTITY;
    }

    @Override
    public int compare(IGridStack left, IGridStack right, SortingDirection sortingDirection) {
        int leftSize = left.getQuantity();
        int rightSize = right.getQuantity();

        if (leftSize != rightSize) {
            if (sortingDirection == SortingDirection.ASCENDING) {
                return (leftSize > rightSize) ? 1 : -1;
            } else if (sortingDirection == SortingDirection.DESCENDING) {
                return (rightSize > leftSize) ? 1 : -1;
            }
        }

        return 0;
    }
}
