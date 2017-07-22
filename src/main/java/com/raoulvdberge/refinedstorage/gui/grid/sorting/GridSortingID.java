package com.raoulvdberge.refinedstorage.gui.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GridSortingID extends GridSorting {
    @Override
    public int compare(IGridStack left, IGridStack right) {
        int x = left.getHash();
        int y = right.getHash();
        if (left.getIngredient() instanceof ItemStack && right.getIngredient() instanceof ItemStack) {
            x = Item.getIdFromItem(((ItemStack) left.getIngredient()).getItem());
            y = Item.getIdFromItem(((ItemStack) right.getIngredient()).getItem());
        }

        if (x != y) {
            if (sortingDirection == IGrid.SORTING_DIRECTION_DESCENDING) {
                return Integer.compare(x, y);
            } else if (sortingDirection == IGrid.SORTING_DIRECTION_ASCENDING) {
                return Integer.compare(y, x);
            }
        }

        return 0;
    }
}