package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.function.Predicate;

public class GridFilterMod implements Predicate<IGridStack> {
    private String inputModName;

    public GridFilterMod(String inputModName) {
        this.inputModName = standardify(inputModName);
    }

    @Override
    public boolean test(IGridStack stack) {
        String modId = stack.getModId();

        if (modId != null) {
            if (modId.contains(inputModName)) {
                return true;
            }

            String modName = stack.getModName();
            if (modName != null) {
                modName = standardify(modName);

                if (modName.contains(inputModName)) {
                    return true;
                }
            }
        }

        return false;
    }

    private String standardify(String input) {
        return input.toLowerCase().replace(" ", "");
    }
}
