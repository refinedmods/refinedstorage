package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.screen.widget.sidebutton.CrafterModeSideButton;
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener;

public class CrafterTileDataParameterClientListener implements TileDataParameterClientListener<Boolean> {
    @Override
    public void onChanged(boolean initial, Boolean hasRoot) {
        if (!hasRoot) {
            BaseScreen.executeLater(CrafterScreen.class, gui -> gui.addSideButton(new CrafterModeSideButton(gui)));
        }
    }
}
