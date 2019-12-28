package com.raoulvdberge.refinedstorage.screen.grid.view;

import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.screen.grid.sorting.IGridSorter;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.screen.grid.stack.ItemGridStack;

import java.util.List;

public class ItemGridView extends BaseGridView {
    public ItemGridView(GridScreen screen, IGridSorter defaultSorter, List<IGridSorter> sorters) {
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
        if (!(stack instanceof ItemGridStack)) {
            return;
        }

        // COMMENT 1 (about this if check in general)
        // Update the other id reference if needed.
        // Taking a stack out - and then re-inserting it - gives the new stack a new ID
        // With that new id, the reference for the crafting stack would be outdated.

        // COMMENT 2 (about map.containsKey(stack.getOtherId()))
        // This check is needed or the .updateOtherId() call will crash with a NPE in high-update environments.
        // This is because we might have scenarios where we process "old" delta packets from another session when we haven't received any initial update packet from the new session.
        // (This is because of the executeLater system)
        // This causes the .updateOtherId() to fail with a NPE because the map is still empty or the IDs mismatch.
        // We could use !map.isEmpty() here too. But if we have 2 "old" delta packets, it would rightfully ignore the first one. But this method mutates the map and would put an entry.
        // This means that on the second delta packet it would still crash because the map wouldn't be empty anymore.
        if (!stack.isCraftable() &&
            stack.getOtherId() != null &&
            map.containsKey(stack.getOtherId())) {
            map.get(stack.getOtherId()).updateOtherId(stack.getId());
        }

        ItemGridStack existing = (ItemGridStack) map.get(stack.getId());

        if (existing == null) {
            ((ItemGridStack) stack).getStack().setCount(delta);

            map.put(stack.getId(), stack);
        } else {
            if (existing.getStack().getCount() + delta <= 0) {
                existing.setZeroed(true);

                map.remove(existing.getId());
            } else {
                existing.getStack().grow(delta);
            }

            existing.setTrackerEntry(stack.getTrackerEntry());
        }
    }
}
