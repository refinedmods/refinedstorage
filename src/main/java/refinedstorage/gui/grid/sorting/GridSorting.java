package refinedstorage.gui.grid.sorting;

import refinedstorage.gui.grid.ClientStack;

import java.util.Comparator;

public abstract class GridSorting implements Comparator<ClientStack> {
    protected int sortingDirection;

    public void setSortingDirection(int sortingDirection) {
        this.sortingDirection = sortingDirection;
    }
}
