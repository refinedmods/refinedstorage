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

        // COMMENT 1 (about this if check)
        // Update the other id reference if needed.
        // Taking a stack out - and then re-inserting it - gives the new stack a new ID
        // With that new id, the reference for the crafting stack would be outdated.

        // COMMENT 2 (about !map.isEmpty())
        // If we close and reopen the Grid really fast,
        // the server might be notified too late that the client closed the Grid,
        // and thus the server would still sent delta packets
        // because it thinks the Grid is still open.
        // Of course, on the client, the Grid has been closed already.
        // If we would reopen the Grid, it would process all these "old" packets
        // before the initial update packet is handled.
        // (This is because of the BaseScreen.executeLater system)
        // This check ignores the "rogue" packet(s).
        // postChange() is only called by the delta packet.
        // So if we have a situation where the delta packet is handled before the initial update packet was received (if the map is still empty),
        // we'll NOT execute this if block as it would NPE crash (map is still empty thus map.get fails).
        if (!stack.isCraftable() &&
            stack.getOtherId() != null &&
            !map.isEmpty()) {
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
