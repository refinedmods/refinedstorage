package com.raoulvdberge.refinedstorage.screen.grid.view;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.screen.grid.filtering.GridFilterParser;
import com.raoulvdberge.refinedstorage.screen.grid.sorting.IGridSorter;
import com.raoulvdberge.refinedstorage.screen.grid.sorting.SortingDirection;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;

import java.util.*;
import java.util.function.Predicate;

public abstract class BaseGridView implements IGridView {
    private GridScreen screen;
    private boolean canCraft;

    private IGridSorter defaultSorter;
    private List<IGridSorter> sorters;

    private List<IGridStack> stacks = new ArrayList<>();
    protected Map<UUID, IGridStack> map = new HashMap<>();

    public BaseGridView(GridScreen screen, IGridSorter defaultSorter, List<IGridSorter> sorters) {
        this.screen = screen;
        this.defaultSorter = defaultSorter;
        this.sorters = sorters;
    }

    @Override
    public List<IGridStack> getStacks() {
        return stacks;
    }

    @Override
    public void sort() {
        List<IGridStack> stacks = new ArrayList<>();

        if (screen.getGrid().isActive()) {
            stacks.addAll(map.values());

            IGrid grid = screen.getGrid();

            List<Predicate<IGridStack>> filters = GridFilterParser.getFilters(
                grid,
                screen.getSearchFieldText(),
                (grid.getTabSelected() >= 0 && grid.getTabSelected() < grid.getTabs().size()) ? grid.getTabs().get(grid.getTabSelected()).getFilters() : grid.getFilters()
            );

            Iterator<IGridStack> it = stacks.iterator();

            while (it.hasNext()) {
                IGridStack stack = it.next();

                for (Predicate<IGridStack> filter : filters) {
                    if (!filter.test(stack)) {
                        it.remove();

                        break;
                    }
                }
            }

            SortingDirection sortingDirection = grid.getSortingDirection() == IGrid.SORTING_DIRECTION_DESCENDING ? SortingDirection.DESCENDING : SortingDirection.ASCENDING;

            stacks.sort((left, right) -> defaultSorter.compare(left, right, sortingDirection));

            for (IGridSorter sorter : sorters) {
                if (sorter.isApplicable(grid)) {
                    stacks.sort((left, right) -> sorter.compare(left, right, sortingDirection));
                }
            }
        }

        this.stacks = stacks;

        this.screen.updateScrollbar();
    }

    @Override
    public void setCanCraft(boolean canCraft) {
        this.canCraft = canCraft;
    }

    @Override
    public boolean canCraft() {
        return canCraft;
    }
}
