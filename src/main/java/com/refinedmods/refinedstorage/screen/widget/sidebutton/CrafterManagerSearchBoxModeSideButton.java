package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.screen.CrafterManagerScreen;
import com.refinedmods.refinedstorage.blockentity.CrafterManagerBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;

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
        BlockEntitySynchronizationManager.setParameter(CrafterManagerBlockEntity.SEARCH_BOX_MODE, mode);
    }
}
