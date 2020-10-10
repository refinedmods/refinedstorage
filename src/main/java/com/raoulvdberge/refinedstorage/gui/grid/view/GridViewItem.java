package com.raoulvdberge.refinedstorage.gui.grid.view;

import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.IGridSorter;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.List;

public class GridViewItem extends GridViewBase {
    public GridViewItem(GuiGrid gui, IGridSorter defaultSorter, List<IGridSorter> sorters) {
        super(gui, defaultSorter, sorters);
    }

    @Override
    public void postChange(IGridStack stack, int delta) {
        if (!(stack instanceof GridStackItem)) {
            return;
        }

        GridStackItem existing = (GridStackItem) map.get(stack.getHash());

        if (existing == null) {
            ((GridStackItem) stack).getStack().setCount(delta);

            map.put(stack.getHash(), stack);
        } else {
            if (existing.getStack().getCount() + delta <= 0) {
                if (existing.isCraftable()) {
                    existing.setDisplayCraftText(true);
                } else {
                    map.remove(existing.getHash());
                }
            } else {
                if (existing.doesDisplayCraftText()) {
                    existing.setDisplayCraftText(false);

                    existing.getStack().setCount(delta);
                } else {
                    existing.getStack().grow(delta);
                }
            }

            existing.setTrackerEntry(stack.getTrackerEntry());
        }
    }
}
