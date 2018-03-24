package com.raoulvdberge.refinedstorage.gui.grid.view;

import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.IGridSorter;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.List;

public class GridViewFluid extends GridViewBase {
    public GridViewFluid(GuiGrid gui, IGridSorter defaultSorter, List<IGridSorter> sorters) {
        super(gui, defaultSorter, sorters);
    }

    @Override
    public void setStacks(List<IGridStack> stacks) {
        map.clear();

        for (IGridStack stack : stacks) {
            map.put(stack.getHash(), stack);
        }
    }

    @Override
    public void postChange(IGridStack stack, int delta) {
        GridStackFluid existing = (GridStackFluid) map.get(stack.getHash());

        if (existing == null) {
            map.put(stack.getHash(), stack);
        } else {
            if (existing.getStack().amount + delta <= 0) {
                map.remove(existing.getHash());
            } else {
                existing.getStack().amount += delta;
            }

            existing.setTrackerEntry(stack.getTrackerEntry());
        }
    }
}
