package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.function.Predicate;

public class GridFilterMod implements Predicate<IGridStack> {
    private String modName;

    public GridFilterMod(String modName) {
        this.modName = modName.toLowerCase().replace(" ", "");
    }

    private String getModNameFromModId(String id) {
        ModContainer container = Loader.instance().getActiveModList().stream()
            .filter(m -> m.getModId().toLowerCase().equals(id))
            .findFirst()
            .orElse(null);

        return container == null ? id : container.getName().toLowerCase().replace(" ", "");
    }

    @Override
    public boolean test(IGridStack stack) {
        String otherModId = stack.getModId().toLowerCase();

        if (!getModNameFromModId(otherModId).contains(modName)) {
            return stack.getModId().contains(modName);
        }

        return true;
    }
}
