package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilter;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.item.ItemGridFilter;

import java.util.List;
import java.util.function.Predicate;

public class GridFilterFilteredItems implements Predicate<IGridStack> {
    private List<GridFilter> filteredItems;

    public GridFilterFilteredItems(List<GridFilter> filteredItems) {
        this.filteredItems = filteredItems;
    }

    @Override
    public boolean test(IGridStack stack) {
        int lastMode = ItemGridFilter.MODE_WHITELIST;

        for (GridFilter filteredItem : filteredItems) {
            lastMode = filteredItem.getMode();

            if (filteredItem.isModFilter()) {
                if (filteredItem.getStack().getItem().getRegistryName().getResourceDomain().equalsIgnoreCase(stack.getModId())) {
                    return filteredItem.getMode() == ItemGridFilter.MODE_WHITELIST;
                }
            } else if (API.instance().getComparer().isEqual(((GridStackItem) stack).getStack(), filteredItem.getStack(), filteredItem.getCompare())) {
                return filteredItem.getMode() == ItemGridFilter.MODE_WHITELIST;
            }
        }

        return lastMode != ItemGridFilter.MODE_WHITELIST;
    }
}
