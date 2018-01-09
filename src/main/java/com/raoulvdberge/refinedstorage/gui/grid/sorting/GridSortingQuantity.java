package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;

public class GridSortingQuantity extends GridSorting {
    @Override
    public int compare(IGridStack left, IGridStack right) {
        int leftSize = isStackCraftableEmpty(left) ? 0 : left.getQuantity();
        int rightSize = isStackCraftableEmpty(right) ? 0 : right.getQuantity();

        if (leftSize != rightSize) {
            if (sortingDirection == IGrid.SORTING_DIRECTION_ASCENDING) {
                return (leftSize > rightSize) ? 1 : -1;
            } else if (sortingDirection == IGrid.SORTING_DIRECTION_DESCENDING) {
                return (rightSize > leftSize) ? 1 : -1;
            }
        }

        return 0;
    }
    
    private boolean isStackCraftableEmpty(IGridStack stack) {
        if (!(stack instanceof GridStackItem))
            return false;
        return ((GridStackItem)stack).doesDisplayCraftText();
    }
}
