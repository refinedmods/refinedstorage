package com.raoulvdberge.refinedstorage.gui;

public interface IResizableDisplay {
    int getVisibleRows();

    int getRows();

    int getCurrentOffset();

    String getSearchFieldText();

    int getTopHeight();

    int getBottomHeight();

    int getYPlayerInventory();
}
