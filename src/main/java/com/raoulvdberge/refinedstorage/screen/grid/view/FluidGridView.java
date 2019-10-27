package com.raoulvdberge.refinedstorage.screen.grid.view;

import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.screen.grid.sorting.IGridSorter;
import com.raoulvdberge.refinedstorage.screen.grid.stack.FluidGridStack;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;

import java.util.List;

public class FluidGridView extends BaseGridView {
    public FluidGridView(GridScreen screen, IGridSorter defaultSorter, List<IGridSorter> sorters) {
        super(screen, defaultSorter, sorters);
    }

    @Override
    public void setStacks(List<IGridStack> stacks) {
        map.clear();

        for (IGridStack stack : stacks) {
            map.put(stack.getId(), stack);
        }
    }

    @Override
    public void postChange(IGridStack stack, int delta) {
        if (!(stack instanceof FluidGridStack)) {
            return;
        }

        FluidGridStack existing = (FluidGridStack) map.get(stack.getId());

        if (existing == null) {
            ((FluidGridStack) stack).getStack().setAmount(delta);

            map.put(stack.getId(), stack);
        } else {
            if (existing.getStack().getAmount() + delta <= 0) {
                existing.getStack().grow(delta);

                map.remove(existing.getId());
            } else {
                existing.getStack().grow(delta);
            }

            existing.setTrackerEntry(stack.getTrackerEntry());
        }
    }
}
