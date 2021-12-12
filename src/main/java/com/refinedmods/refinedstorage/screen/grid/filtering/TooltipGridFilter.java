package com.refinedmods.refinedstorage.screen.grid.filtering;

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Predicate;

public class TooltipGridFilter implements Predicate<IGridStack> {
    private final String tooltip;

    public TooltipGridFilter(String tooltip) {
        this.tooltip = tooltip.toLowerCase();
    }

    @Override
    public boolean test(IGridStack stack) {
        List<Component> stackTooltip = stack.getTooltip(false);

        for (int i = 1; i < stackTooltip.size(); ++i) {
            if (stackTooltip.get(i).getString().toLowerCase().contains(tooltip.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
