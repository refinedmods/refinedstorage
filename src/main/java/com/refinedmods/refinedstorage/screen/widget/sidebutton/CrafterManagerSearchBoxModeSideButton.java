package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.screen.CrafterManagerScreen;
import com.refinedmods.refinedstorage.tile.CrafterManagerTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

public class CrafterManagerSearchBoxModeSideButton extends SearchBoxModeSideButton {
    public CrafterManagerSearchBoxModeSideButton(CrafterManagerScreen screen) {
        super(screen);
    }

    @Override
    protected int getSearchBoxMode() {
        return ((CrafterManagerScreen) screen).getCrafterManager().getSearchBoxMode();
    }

    @Override
    protected void setSearchBoxMode(int mode) {
        TileDataManager.setParameter(CrafterManagerTile.SEARCH_BOX_MODE, mode);
    }
}
