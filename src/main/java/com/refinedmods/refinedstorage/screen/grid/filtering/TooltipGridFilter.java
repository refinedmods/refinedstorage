package com.refinedmods.refinedstorage.screen.grid.filtering;

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.function.Predicate;

public class TooltipGridFilter implements Predicate<IGridStack> {
    private final String tooltip;

    public TooltipGridFilter(String tooltip) {
        this.tooltip = tooltip.toLowerCase();
    }

    @Override
    public boolean test(IGridStack stack) {
        List<ITextComponent> tooltip = stack.getTooltip(false);

        for (int i = 1; i < tooltip.size(); ++i) {
            if (tooltip.get(i).getString().toLowerCase().contains(this.tooltip.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
