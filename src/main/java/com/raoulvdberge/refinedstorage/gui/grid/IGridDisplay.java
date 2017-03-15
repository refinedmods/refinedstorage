package com.raoulvdberge.refinedstorage.gui.grid;

public interface IGridDisplay {
    int getVisibleRows();

    int getRows();

    int getHeader();

    int getFooter();

    int getYPlayerInventory();

    void eatItem(boolean food);
}
