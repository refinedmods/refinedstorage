package com.raoulvdberge.refinedstorage.gui.grid.view;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.filtering.GridFilterParser;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.GridSorterDirection;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.IGridSorter;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.*;
import java.util.function.Predicate;

public abstract class GridViewBase implements IGridView {
    private GuiGrid gui;
    private boolean canCraft;

    private IGridSorter defaultSorter;
    private List<IGridSorter> sorters;

    private List<IGridStack> stacks = new ArrayList<>();
    protected Map<Integer, IGridStack> map = new HashMap<>();

    public GridViewBase(GuiGrid gui, IGridSorter defaultSorter, List<IGridSorter> sorters) {
        this.gui = gui;
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

        if (gui.getGrid().isActive()) {
            stacks.addAll(map.values());

            IGrid grid = gui.getGrid();

            List<Predicate<IGridStack>> filters = GridFilterParser.getFilters(
                grid,
                gui.getSearchField() != null ? gui.getSearchField().getText() : "",
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

            GridSorterDirection sortingDirection = grid.getSortingDirection() == IGrid.SORTING_DIRECTION_DESCENDING ? GridSorterDirection.DESCENDING : GridSorterDirection.ASCENDING;

            stacks.sort((left, right) -> defaultSorter.compare(left, right, sortingDirection));

            for (IGridSorter sorter : sorters) {
                if (sorter.isApplicable(grid)) {
                    stacks.sort((left, right) -> sorter.compare(left, right, sortingDirection));
                }
            }
        }

        this.stacks = stacks;

        this.gui.updateScrollbarAndTabs();
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
