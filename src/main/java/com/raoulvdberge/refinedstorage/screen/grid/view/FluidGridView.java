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
            // Don't let a craftable stack override a normal stack
            if (stack.doesDisplayCraftText() && map.containsKey(stack.getHash())) {
                continue;
            }

            map.put(stack.getHash(), stack);
        }
    }

    @Override
    public void postChange(IGridStack stack, int delta) {
        if (!(stack instanceof FluidGridStack)) {
            return;
        }

        FluidGridStack existing = (FluidGridStack) map.get(stack.getHash());

        if (existing == null) {
            ((FluidGridStack) stack).getStack().setAmount(delta);

            map.put(stack.getHash(), stack);
        } else {
            if (existing.getStack().getAmount() + delta <= 0) {
                if (existing.isCraftable()) {
                    existing.setDisplayCraftText(true);
                } else {
                    map.remove(existing.getHash());
                }
            } else {
                if (existing.doesDisplayCraftText()) {
                    existing.setDisplayCraftText(false);

                    existing.getStack().setAmount(delta);
                } else {
                    existing.getStack().grow(delta);
                }
            }

            existing.setTrackerEntry(stack.getTrackerEntry());
        }
    }
}
