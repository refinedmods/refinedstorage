package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.gui.control.SideButtonCrafterMode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameterClientListener;

public class TileDataParameterClientListenerCrafter implements TileDataParameterClientListener<Boolean> {
    @Override
    public void onChanged(boolean initial, Boolean hasRoot) {
        if (!hasRoot) {
            GuiBase.executeLater(GuiCrafter.class, gui -> {
                gui.addSideButton(new SideButtonCrafterMode(gui));
            });
        }
    }
}
