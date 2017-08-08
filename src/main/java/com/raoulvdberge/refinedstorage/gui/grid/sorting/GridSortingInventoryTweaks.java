package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import invtweaks.api.InvTweaksAPI;
import net.minecraftforge.fml.common.Loader;

public class GridSortingInventoryTweaks extends GridSorting {
    public static final String MOD_ID = "inventorytweaks";

    private InvTweaksAPI api = null;

    public GridSortingInventoryTweaks() {
        try {
            api = (InvTweaksAPI) Class.forName("invtweaks.forge.InvTweaksMod", true, Loader.instance().getModClassLoader()).getField("instance").get(null);
        } catch (Exception e) {
            // NO OP
        }
    }

    @Override
    public int compare(IGridStack left, IGridStack right) {
        if (api != null && left instanceof GridStackItem && right instanceof GridStackItem) {
            if (sortingDirection == NetworkNodeGrid.SORTING_DIRECTION_DESCENDING) {
                return api.compareItems(((GridStackItem) left).getStack(), ((GridStackItem) right).getStack());
            } else if (sortingDirection == NetworkNodeGrid.SORTING_DIRECTION_ASCENDING) {
                return api.compareItems(((GridStackItem) right).getStack(), ((GridStackItem) left).getStack());
            }
        }

        return 0;
    }
}