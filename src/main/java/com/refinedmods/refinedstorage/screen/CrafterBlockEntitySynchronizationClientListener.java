package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.screen.widget.sidebutton.CrafterModeSideButton;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationClientListener;

public class CrafterBlockEntitySynchronizationClientListener implements BlockEntitySynchronizationClientListener<Boolean> {
    @Override
    public void onChanged(boolean initial, Boolean hasRoot) {
        if (Boolean.FALSE.equals(hasRoot)) {
            BaseScreen.executeLater(CrafterScreen.class, gui -> gui.addSideButton(new CrafterModeSideButton(gui)));
        }
    }
}
