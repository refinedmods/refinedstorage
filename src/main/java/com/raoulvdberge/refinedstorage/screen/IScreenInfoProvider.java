package com.raoulvdberge.refinedstorage.screen;

public interface IScreenInfoProvider {
    int getVisibleRows();

    int getRows();

    int getCurrentOffset();

    String getSearchFieldText();

    int getTopHeight();

    int getBottomHeight();

    int getYPlayerInventory();
}
