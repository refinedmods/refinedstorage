package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonCrafterMode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameterClientListener;

public class TileDataParameterClientListenerCrafter implements TileDataParameterClientListener<Boolean> {
    @Override
    public void onChanged(boolean initial, Boolean hasRoot) {
        if (!hasRoot) {
            BaseScreen.executeLater(GuiCrafter.class, gui -> {
                gui.addSideButton(new SideButtonCrafterMode(gui));
            });
        }
    }
}
