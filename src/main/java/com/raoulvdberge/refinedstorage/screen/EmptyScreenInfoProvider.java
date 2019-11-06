package com.raoulvdberge.refinedstorage.screen;

public class EmptyScreenInfoProvider implements IScreenInfoProvider {
    @Override
    public int getVisibleRows() {
        return 3;
    }

    @Override
    public int getRows() {
        return 0;
    }

    @Override
    public int getCurrentOffset() {
        return 0;
    }

    @Override
    public String getSearchFieldText() {
        return "";
    }

    @Override
    public int getTopHeight() {
        return 0;
    }

    @Override
    public int getBottomHeight() {
        return 0;
    }

    @Override
    public int getYPlayerInventory() {
        return 0;
    }
}
