package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.ClientStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IClientStack;

public class GridFilterCraftable implements IGridFilter {
    private boolean craftable;

    public GridFilterCraftable(boolean craftable) {
        this.craftable = craftable;
    }

    @Override
    public boolean accepts(IClientStack stack) {
        return stack instanceof ClientStackItem && ((ClientStackItem) stack).isCraftable() == craftable;
    }

    @Override
    public boolean isStrong() {
        return true;
    }
}
