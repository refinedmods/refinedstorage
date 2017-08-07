package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import invtweaks.api.InvTweaksAPI;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class GridSortingInventoryTweaks extends GridSorting {
    private InvTweaksAPI api = null;

    public GridSortingInventoryTweaks() {
        try {
            api = (InvTweaksAPI) Class.forName("invtweaks.forge.InvTweaksMod", true, Loader.instance().getModClassLoader()).getField("instance").get(null);
        } catch (Exception ex) {
            LogManager.getLogger(getClass()).log(Level.ERROR, "Error with InventoryTweak sorting", ex);
        }
    }
    
    @Override
    public int compare(IGridStack o1, IGridStack o2) {
        if (api != null && o1 instanceof GridStackItem && o2 instanceof GridStackItem) {
            if (sortingDirection == NetworkNodeGrid.SORTING_DIRECTION_DESCENDING) {
                return api.compareItems(((GridStackItem) o1).getStack(), ((GridStackItem) o2).getStack());
            } else if (sortingDirection == NetworkNodeGrid.SORTING_DIRECTION_ASCENDING) {
                return api.compareItems(((GridStackItem) o2).getStack(), ((GridStackItem) o1).getStack());
            }
        }
        return 0;
    }
}