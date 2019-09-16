package com.raoulvdberge.refinedstorage.screen.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GridSorterID implements IGridSorter {
    @Override
    public boolean isApplicable(IGrid grid) {
        return grid.getSortingType() == IGrid.SORTING_TYPE_ID;
    }

    @Override
    public int compare(IGridStack left, IGridStack right, GridSorterDirection sortingDirection) {
        int x = left.getHash();
        int y = right.getHash();

        if (left.getIngredient() instanceof ItemStack && right.getIngredient() instanceof ItemStack) {
            x = Item.getIdFromItem(((ItemStack) left.getIngredient()).getItem());
            y = Item.getIdFromItem(((ItemStack) right.getIngredient()).getItem());
        }

        if (x != y) {
            if (sortingDirection == GridSorterDirection.DESCENDING) {
                return Integer.compare(x, y);
            } else if (sortingDirection == GridSorterDirection.ASCENDING) {
                return Integer.compare(y, x);
            }
        }

        return 0;
    }
}