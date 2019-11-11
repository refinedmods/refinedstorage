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

        // Update the other id reference if needed.
        // Taking a stack out - and then re-inserting it - gives the new stack a new ID
        // With that new id, the reference for the crafting stack would be outdated.
        if (!stack.isCraftable() &&
            stack.getOtherId() != null) {
            map.get(stack.getOtherId()).updateOtherId(stack.getId());
        }

        FluidGridStack existing = (FluidGridStack) map.get(stack.getId());

        if (existing == null) {
            ((FluidGridStack) stack).getStack().setAmount(delta);

            map.put(stack.getId(), stack);
        } else {
            if (existing.getStack().getAmount() + delta <= 0) {
                existing.setZeroed(true);

                map.remove(existing.getId());
            } else {
                existing.getStack().grow(delta);
            }

            existing.setTrackerEntry(stack.getTrackerEntry());
        }
    }
}
