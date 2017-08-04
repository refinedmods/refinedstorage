package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraftforge.fml.common.Loader;

/**
 * A GridSorting implementation for the Inventory Tweaks sorting mod
 * @author Cooliojazz
 */
public class GridSortingInventoryTweaks extends GridSorting {

    @Override
    public int compare(IGridStack o1, IGridStack o2) {
        try {
            invtweaks.api.InvTweaksAPI api = (invtweaks.api.InvTweaksAPI)Class.forName("invtweaks.forge.InvTweaksMod", true, Loader.instance().getModClassLoader()).getField("instance").get(null);
            if (o1 instanceof GridStackItem && o2 instanceof GridStackItem && api != null) {
                if (sortingDirection == NetworkNodeGrid.SORTING_DIRECTION_DESCENDING) {
                    return api.compareItems(((GridStackItem)o1).getStack(), ((GridStackItem)o2).getStack());
                } else if (sortingDirection == NetworkNodeGrid.SORTING_DIRECTION_ASCENDING) {
                    return api.compareItems(((GridStackItem)o2).getStack(), ((GridStackItem)o1).getStack());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GridSortingInventoryTweaks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
}
