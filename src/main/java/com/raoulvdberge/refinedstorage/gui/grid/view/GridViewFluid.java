package com.raoulvdberge.refinedstorage.gui.grid.view;

import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.IGridSorter;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridViewFluid extends GridViewBase {
    private Map<Integer, GridStackFluid> map = new HashMap<>();
    private List<IGridStack> stacks;
    private GuiGrid gui;
    private List<IGridSorter> sorters;
    private boolean canCraft;

    public GridViewFluid(GuiGrid gui, List<IGridSorter> sorters) {
        this.sorters = sorters;
        this.gui = gui;
    }

    @Override
    public List<IGridStack> getStacks() {
        return stacks;
    }

    @Override
    public void setStacks(List<IGridStack> stacks) {
        map.clear();

        for (IGridStack stack : stacks) {
            map.put(stack.getHash(), (GridStackFluid) stack);
        }
    }

    @Override
    public void postChange(IGridStack stack, int delta) {
        GridStackFluid existing = map.get(stack.getHash());

        if (existing == null) {
            map.put(stack.getHash(), (GridStackFluid) stack);
        } else {
            if (existing.getStack().amount + delta <= 0) {
                map.remove(existing.getHash());
            } else {
                existing.getStack().amount += delta;
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
