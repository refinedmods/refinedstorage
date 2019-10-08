package com.raoulvdberge.refinedstorage.screen.grid.sorting;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IdGridSorter implements IGridSorter {
    @Override
    public boolean isApplicable(IGrid grid) {
        return grid.getSortingType() == IGrid.SORTING_TYPE_ID;
    }

    @Override
    public int compare(IGridStack left, IGridStack right, SortingDirection sortingDirection) {
        int x = left.getHash();
        int y = right.getHash();

        if (left.getIngredient() instanceof ItemStack && right.getIngredient() instanceof ItemStack) {
            x = Item.getIdFromItem(((ItemStack) left.getIngredient()).getItem());
            y = Item.getIdFromItem(((ItemStack) right.getIngredient()).getItem());
        }

        if (x != y) {
            if (sortingDirection == SortingDirection.DESCENDING) {
                return Integer.compare(x, y);
            } else if (sortingDirection == SortingDirection.ASCENDING) {
                return Integer.compare(y, x);
            }
        }

        return 0;
    }
}