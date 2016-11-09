package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilteredItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.ClientStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IClientStack;

public class GridFilterFilteredItem implements IGridFilter {
    private GridFilteredItem filteredItem;

    public GridFilterFilteredItem(GridFilteredItem filteredItem) {
        this.filteredItem = filteredItem;
    }

    @Override
    public boolean accepts(IClientStack stack) {
        return API.instance().getComparer().isEqual(((ClientStackItem) stack).getStack(), filteredItem.getStack(), filteredItem.getCompare());
    }

    @Override
    public boolean isStrong() {
        return true;
    }
}
