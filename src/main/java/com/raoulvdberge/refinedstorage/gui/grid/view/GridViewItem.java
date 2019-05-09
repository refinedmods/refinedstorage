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
    public void setStacks(List<IGridStack> stacks) {
        map.clear();

        for (IGridStack stack : stacks) {
            // Don't let a craftable stack override a normal stack
            if (stack.doesDisplayCraftText() && map.containsKey(stack.getHash())) {
                continue;
            }

            map.put(stack.getHash(), stack);
        }
    }

    @Override
    public void postChange(IGridStack stack, int delta) {
        if (!(stack instanceof GridStackItem)) {
            return;
        }

        GridStackItem existing = (GridStackItem) map.get(stack.getHash());

        if (existing == null) {
            if(delta != 0) {
                ((GridStackItem) stack).getStack().setCount(delta);
            }

            map.put(stack.getHash(), stack);
        } else {
            existing.setCraftable(stack.isCraftable());
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
