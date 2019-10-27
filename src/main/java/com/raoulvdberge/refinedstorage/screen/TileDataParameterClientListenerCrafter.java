package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.CrafterModeSideButton;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameterClientListener;

public class TileDataParameterClientListenerCrafter implements TileDataParameterClientListener<Boolean> {
    @Override
    public void onChanged(boolean initial, Boolean hasRoot) {
        if (!hasRoot) {
            BaseScreen.executeLater(CrafterScreen.class, gui -> {
                gui.addSideButton(new CrafterModeSideButton(gui));
            });
        }
    }
}
