package com.raoulvdberge.refinedstorage.screen.grid.filtering;

import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;

import java.util.Arrays;
import java.util.function.Predicate;

public class OredictGridFilter implements Predicate<IGridStack> {
    private String oreName;

    public OredictGridFilter(String oreName) {
        this.oreName = oreName.toLowerCase();
    }

    @Override
    public boolean test(IGridStack stack) {
        return Arrays.stream(stack.getOreIds()).anyMatch(oreName -> oreName.toLowerCase().contains(this.oreName));
    }
}