package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilter;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.List;
import java.util.function.Predicate;

public class GridFilterFilteredItems implements Predicate<IGridStack> {
    private List<GridFilter> filteredItems;

    public GridFilterFilteredItems(List<GridFilter> filteredItems) {
        this.filteredItems = filteredItems;
    }

    @Override
    public boolean test(IGridStack stack) {
        for (GridFilter filteredItem : filteredItems) {
            if (API.instance().getComparer().isEqual(((GridStackItem) stack).getStack(), filteredItem.getStack(), filteredItem.getCompare())) {
                return true;
            }
        }

        return false;
    }
}
