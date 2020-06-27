package com.refinedmods.refinedstorage.screen.grid.filtering;

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

import java.util.function.Predicate;

public class TooltipGridFilter implements Predicate<IGridStack> {
    private final String tooltip;

    public TooltipGridFilter(String tooltip) {
        this.tooltip = tooltip.toLowerCase();
    }

    @Override
    public boolean test(IGridStack stack) {
        String otherTooltip = stack.getTooltip().trim().toLowerCase();

        if (!otherTooltip.contains("\n")) {
            return false;
        }

        otherTooltip = otherTooltip.substring(otherTooltip.indexOf('\n') + 1); // Remove the first line as that states the item name

        return otherTooltip.contains(tooltip);
    }
}
