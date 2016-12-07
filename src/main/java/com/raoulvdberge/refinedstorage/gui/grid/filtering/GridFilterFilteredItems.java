package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilteredItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.ClientStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IClientStack;
import com.raoulvdberge.refinedstorage.item.ItemGridFilter;

import java.util.List;

public class GridFilterFilteredItems implements IGridFilter {
    private List<GridFilteredItem> filteredItems;

    public GridFilterFilteredItems(List<GridFilteredItem> filteredItems) {
        this.filteredItems = filteredItems;
    }

    @Override
    public boolean accepts(IClientStack stack) {
        int lastMode = ItemGridFilter.MODE_WHITELIST;

        for (GridFilteredItem filteredItem : filteredItems) {
            lastMode = filteredItem.getMode();

            if (filteredItem.isModFilter()) {
                if (filteredItem.getStack().getItem().getRegistryName().getResourceDomain().equalsIgnoreCase(stack.getModId())) {
                    return filteredItem.getMode() == ItemGridFilter.MODE_WHITELIST;
                }
            } else if (API.instance().getComparer().isEqual(((ClientStackItem) stack).getStack(), filteredItem.getStack(), filteredItem.getCompare())) {
                return filteredItem.getMode() == ItemGridFilter.MODE_WHITELIST;
            }
        }

        return lastMode != ItemGridFilter.MODE_WHITELIST;
    }
}
