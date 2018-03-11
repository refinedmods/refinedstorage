package com.raoulvdberge.refinedstorage.gui.grid.view;

import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.IGridSorter;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridViewItem extends GridViewBase {
    private Map<Integer, GridStackItem> map = new HashMap<>();
    private List<IGridStack> stacks;
    private GuiGrid gui;
    private List<IGridSorter> sorters;
    private boolean canCraft;

    public GridViewItem(GuiGrid gui, List<IGridSorter> sorters) {
        this.gui = gui;
        this.sorters = sorters;
    }

    @Override
    public List<IGridStack> getStacks() {
        return stacks;
    }

    @Override
    public void setStacks(List<IGridStack> stacks) {
        map.clear();

        for (IGridStack stack : stacks) {
            // Don't let a craftable stack override a normal stack
            if (((GridStackItem) stack).doesDisplayCraftText() && map.containsKey(stack.getHash())) {
                continue;
            }

            map.put(stack.getHash(), (GridStackItem) stack);
        }
    }

    @Override
    public void postChange(IGridStack stack, int delta) {
        GridStackItem existing = map.get(stack.getHash());

        if (existing == null) {
            ((GridStackItem) stack).getStack().setCount(delta);

            map.put(stack.getHash(), (GridStackItem) stack);
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

    @Override
    public void setCanCraft(boolean canCraft) {
        this.canCraft = canCraft;
    }

    @Override
    public boolean canCraft() {
        return canCraft;
    }

    @Override
    public void sort() {
        List<IGridStack> stacks = new ArrayList<>();

        if (gui.getGrid().isActive()) {
            stacks.addAll(map.values());

            new Thread(() -> sortAndFilter(gui, stacks, sorters)).start();
        }

        this.stacks = stacks;

        updateUI(gui);
    }
}
