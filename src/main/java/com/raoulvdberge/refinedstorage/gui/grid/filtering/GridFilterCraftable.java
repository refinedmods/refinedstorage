package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.function.Predicate;

public class GridFilterCraftable implements Predicate<IGridStack> {
    private boolean craftable;

    public GridFilterCraftable(boolean craftable) {
        this.craftable = craftable;
    }

    @Override
    public boolean test(IGridStack stack) {
        return stack instanceof GridStackItem && ((GridStackItem) stack).isCraftable() == craftable;
    }
}
