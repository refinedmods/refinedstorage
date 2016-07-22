package refinedstorage.gui.grid.sorting;

import refinedstorage.gui.grid.ClientStack;
import refinedstorage.tile.grid.TileGrid;

public class GridSortingName extends GridSorting {
    @Override
    public int compare(ClientStack left, ClientStack right) {
        String leftName = left.getStack().getDisplayName();
        String rightName = right.getStack().getDisplayName();

        if (sortingDirection == TileGrid.SORTING_DIRECTION_ASCENDING) {
            return leftName.compareTo(rightName);
        } else if (sortingDirection == TileGrid.SORTING_DIRECTION_DESCENDING) {
            return rightName.compareTo(leftName);
        }

        return 0;
    }
}
