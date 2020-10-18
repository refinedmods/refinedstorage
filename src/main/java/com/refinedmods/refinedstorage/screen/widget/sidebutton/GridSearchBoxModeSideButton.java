package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.screen.grid.GridScreen;

public class GridSearchBoxModeSideButton extends SearchBoxModeSideButton {
    public GridSearchBoxModeSideButton(GridScreen screen) {
        super(screen);
    }

    @Override
    protected int getSearchBoxMode() {
        return ((GridScreen) screen).getGrid().getSearchBoxMode();
    }

    @Override
    protected void setSearchBoxMode(int mode) {
        ((GridScreen) screen).getGrid().onSearchBoxModeChanged(mode);
        ((GridScreen) screen).getSearchField().setMode(mode);
    }
}
