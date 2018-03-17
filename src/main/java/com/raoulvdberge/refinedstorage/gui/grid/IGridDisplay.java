package com.raoulvdberge.refinedstorage.gui.grid;

public interface IGridDisplay {
    int getVisibleRows();

    int getRows();

    int getCurrentOffset();

    String getSearchFieldText();

    int getHeader();

    int getFooter();

    int getYPlayerInventory();
}
