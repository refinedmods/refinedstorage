package refinedstorage.gui.grid.sorting;

import refinedstorage.gui.grid.ClientStack;
import refinedstorage.tile.grid.TileGrid;

public class GridSortingQuantity extends GridSorting {
    @Override
    public int compare(ClientStack left, ClientStack right) {
        int leftSize = left.getStack().stackSize;
        int rightSize = right.getStack().stackSize;

        if (leftSize != rightSize) {
            if (sortingDirection == TileGrid.SORTING_DIRECTION_ASCENDING) {
                return (leftSize > rightSize) ? 1 : -1;
            } else if (sortingDirection == TileGrid.SORTING_DIRECTION_DESCENDING) {
                return (rightSize > leftSize) ? 1 : -1;
            }
        }

        return 0;
    }
}
