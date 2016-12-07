package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.GridFilteredItem;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;

import java.util.LinkedList;
import java.util.List;

public class GridFilterParser {
    public static List<IGridFilter> getFilters(IGrid grid, String query, List<GridFilteredItem> itemFilters) {
        List<IGridFilter> filters = new LinkedList<>();

        for (String part : query.toLowerCase().trim().split(" ")) {
            if (part.startsWith("@")) {
                filters.add(new GridFilterMod(part.substring(1)));
            } else if (part.startsWith("#")) {
                filters.add(new GridFilterTooltip(part.substring(1)));
            } else {
                filters.add(new GridFilterName(part));
            }
        }

        if (grid.getViewType() == TileGrid.VIEW_TYPE_NON_CRAFTABLES) {
            filters.add(new GridFilterCraftable(false));
        } else if (grid.getViewType() == TileGrid.VIEW_TYPE_CRAFTABLES) {
            filters.add(new GridFilterCraftable(true));
        }

        if (!itemFilters.isEmpty()) {
            filters.add(new GridFilterFilteredItems(itemFilters));
        }

        return filters;
    }
}
