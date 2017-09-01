package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.function.Predicate;

public class GridFilterTooltip implements Predicate<IGridStack> {
    private String tooltip;

    public GridFilterTooltip(String tooltip) {
        this.tooltip = tooltip.toLowerCase();
    }

    @Override
    public boolean test(IGridStack stack) {
        String otherTooltip = stack.getTooltip(false).trim().toLowerCase();

        if (!otherTooltip.contains("\n")) {
            return false;
        }

        otherTooltip = otherTooltip.substring(otherTooltip.indexOf('\n') + 1); // Remove the first line as that states the item name

        return otherTooltip.contains(tooltip);
    }
}
