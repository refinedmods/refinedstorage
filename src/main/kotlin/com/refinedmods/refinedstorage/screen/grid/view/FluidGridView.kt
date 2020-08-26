package com.refinedmods.refinedstorage.screen.grid.view

import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.screen.grid.sorting.IGridSorter
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack

class FluidGridView(screen: GridScreen, defaultSorter: IGridSorter, sorters: List<IGridSorter>) : BaseGridView(screen, defaultSorter, sorters) {
    override fun setStacks(stacks: List<IGridStack>) {
        map.clear()
        for (stack in stacks) {
            map[stack.id] = stack
        }
    }

    override fun postChange(stack: IGridStack, delta: Int) {
        if (stack !is FluidGridStack) {
            return
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
        if (!stack.isCraftable() && stack.getOtherId() != null &&
                map.containsKey(stack.getOtherId())) {
            val craftingStack = map[stack.getOtherId()]
            craftingStack!!.updateOtherId(stack.getId())
            craftingStack.trackerEntry = stack.getTrackerEntry()
        }
        val existing = map[stack.getId()] as FluidGridStack?
        if (existing == null) {
            stack.stack.setAmount(delta)
            map[stack.getId()] = stack
        } else {
            if (existing.stack.getAmount() + delta <= 0) {
                existing.setZeroed(true)
                map.remove(existing.id)
            } else {
                existing.stack.grow(delta)
            }
            existing.trackerEntry = stack.getTrackerEntry()
        }
    }
}