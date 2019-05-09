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
            // Don't let a craftable stack override a normal stack
            if (stack.doesDisplayCraftText() && map.containsKey(stack.getHash())) {
                continue;
            }

            map.put(stack.getHash(), stack);
        }
    }

    @Override
    public void postChange(IGridStack stack, int delta) {
        if (!(stack instanceof GridStackFluid)) {
            return;
        }

        GridStackFluid existing = (GridStackFluid) map.get(stack.getHash());

        if (existing == null) {
            ((GridStackFluid) stack).getStack().amount = delta;

            map.put(stack.getHash(), stack);
        } else {
            existing.setCraftable(stack.isCraftable());
            if (existing.getStack().amount + delta <= 0) {
                if (existing.isCraftable()) {
                    existing.setDisplayCraftText(true);
                } else {
                    map.remove(existing.getHash());
                }
            } else {
                if (existing.doesDisplayCraftText()) {
                    existing.setDisplayCraftText(false);

                    existing.getStack().amount = delta;
                } else {
                    existing.getStack().amount += delta;
                }
            }

            existing.setTrackerEntry(stack.getTrackerEntry());
        }
    }
}
