package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IClientStack;

public class GridFilterTooltip implements IGridFilter {
    private String tooltip;

    public GridFilterTooltip(String tooltip) {
        this.tooltip = tooltip.toLowerCase();
    }

    @Override
    public boolean accepts(IClientStack stack) {
        String otherTooltip = stack.getTooltip().trim().toLowerCase();

        if (!otherTooltip.contains("\n")) {
            return false;
        }

        otherTooltip = otherTooltip.substring(otherTooltip.indexOf('\n') + 1); // Remove the first line as that states the item name

        return otherTooltip.contains(tooltip);
    }
}
