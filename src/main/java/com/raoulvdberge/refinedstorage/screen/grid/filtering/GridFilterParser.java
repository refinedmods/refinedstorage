package com.raoulvdberge.refinedstorage.screen.grid.filtering;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public final class GridFilterParser {
    public static List<Predicate<IGridStack>> getFilters(@Nullable IGrid grid, String query, List<IFilter> filters) {
        List<Predicate<IGridStack>> gridFilters;

        String[] orParts = query.split("\\|");

        if (orParts.length == 1) {
            gridFilters = getFilters(query);
        } else {
            List<List<Predicate<IGridStack>>> orPartFilters = new LinkedList<>();

            for (String orPart : orParts) {
                orPartFilters.add(getFilters(orPart));
            }

            gridFilters = new LinkedList<>();
            gridFilters.add(new GridFilterOr(orPartFilters));
        }

        if (grid != null) {
            if (grid.getViewType() == IGrid.VIEW_TYPE_NON_CRAFTABLES) {
                gridFilters.add(new GridFilterCraftable(false));
            } else if (grid.getViewType() == IGrid.VIEW_TYPE_CRAFTABLES) {
                gridFilters.add(new GridFilterCraftable(true));
            }
        }

        if (!filters.isEmpty()) {
            gridFilters.add(new GridFilterFilter(filters));
        }

        return gridFilters;
    }

    private static List<Predicate<IGridStack>> getFilters(String query) {
        List<Predicate<IGridStack>> gridFilters = new LinkedList<>();

        for (String part : query.toLowerCase().trim().split(" ")) {
            if (part.startsWith("@")) {
                gridFilters.add(new GridFilterMod(part.substring(1)));
            } else if (part.startsWith("#")) {
                gridFilters.add(new GridFilterTooltip(part.substring(1)));
            } else if (part.startsWith("$")) {
                gridFilters.add(new GridFilterOreDict(part.substring(1)));
            } else {
                gridFilters.add(new GridFilterName(part));
            }
        }

        return gridFilters;
    }
}
