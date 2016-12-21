package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilter;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class GridFilterParser {
    public static List<Predicate<IGridStack>> getFilters(IGrid grid, String query, List<GridFilter> filteredItems) {
        List<Predicate<IGridStack>> filters = new LinkedList<>();

        for (String part : query.toLowerCase().trim().split(" ")) {
            if (part.startsWith("@")) {
                filters.add(new GridFilterMod(part.substring(1)));
            } else if (part.startsWith("#")) {
                filters.add(new GridFilterTooltip(part.substring(1)));
            } else {
                filters.add(new GridFilterName(part));
            }
        }

        if (grid.getViewType() == NetworkNodeGrid.VIEW_TYPE_NON_CRAFTABLES) {
            filters.add(new GridFilterCraftable(false));
        } else if (grid.getViewType() == NetworkNodeGrid.VIEW_TYPE_CRAFTABLES) {
            filters.add(new GridFilterCraftable(true));
        }

        if (!filteredItems.isEmpty()) {
            filters.add(new GridFilterFilteredItems(filteredItems));
        }

        return filters;
    }
}
