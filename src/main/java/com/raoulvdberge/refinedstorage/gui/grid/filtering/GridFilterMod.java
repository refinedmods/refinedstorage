package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.function.Predicate;

public class GridFilterMod implements Predicate<IGridStack> {
    private String inputModName;

    public GridFilterMod(String inputModName) {
        this.inputModName = inputModName.toLowerCase().replace(" ", "");
    }

    @Override
    public boolean test(IGridStack stack) {
        return stack.getModId().contains(inputModName) || stack.getModName().contains(inputModName);
    }
}
