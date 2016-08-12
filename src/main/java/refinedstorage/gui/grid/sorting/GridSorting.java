package refinedstorage.gui.grid.sorting;

import refinedstorage.gui.grid.stack.IClientStack;

import java.util.Comparator;

public abstract class GridSorting implements Comparator<IClientStack> {
    protected int sortingDirection;

    public void setSortingDirection(int sortingDirection) {
        this.sortingDirection = sortingDirection;
    }
}
