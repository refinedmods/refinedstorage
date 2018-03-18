package com.raoulvdberge.refinedstorage.gui.grid.view;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.filtering.GridFilterParser;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.GridSorterDirection;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.GridSorterName;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.IGridSorter;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.*;
import java.util.function.Predicate;

public abstract class GridViewBase implements IGridView {
    private GuiGrid gui;
    private boolean canCraft;

    private List<IGridSorter> sorters;
    private SorterThread sorterThread = new SorterThread();

    private List<IGridStack> stacks;
    protected Map<Integer, IGridStack> map = new HashMap<>();

    public GridViewBase(GuiGrid gui, List<IGridSorter> sorters) {
        this.gui = gui;
        this.sorters = sorters;
    }

    @Override
    public List<IGridStack> getStacks() {
        return stacks;
    }

    @Override
    public void sort() {
        if (!sorterThread.sorting) {
            new Thread(sorterThread, "RS grid sorting").start();
        }
    }

    private void updateUI(GuiGrid gui) {
        if (gui.getScrollbar() != null) {
            gui.getScrollbar().setEnabled(gui.getRows() > gui.getVisibleRows());
            gui.getScrollbar().setMaxOffset(gui.getRows() - gui.getVisibleRows());
        }

        if (gui.getTabPageLeft() != null) {
            gui.getTabPageLeft().visible = gui.getGrid().getTotalTabPages() > 0;
        }

        if (gui.getTabPageRight() != null) {
            gui.getTabPageRight().visible = gui.getGrid().getTotalTabPages() > 0;
        }
    }

    @Override
    public void setCanCraft(boolean canCraft) {
        this.canCraft = canCraft;
    }

    @Override
    public boolean canCraft() {
        return canCraft;
    }

    private class SorterThread implements Runnable {
        private boolean sorting;

        @Override
        public void run() {
            this.sorting = true;

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

                stacks.sort((left, right) -> new GridSorterName().compare(left, right, sortingDirection));

                sorters.stream().filter(s -> s.isApplicable(grid)).forEach(s -> {
                    stacks.sort((left, right) -> s.compare(left, right, sortingDirection));
                });
            }

            GridViewBase.this.stacks = stacks;

            updateUI(gui);

            this.sorting = false;
        }
    }
}
