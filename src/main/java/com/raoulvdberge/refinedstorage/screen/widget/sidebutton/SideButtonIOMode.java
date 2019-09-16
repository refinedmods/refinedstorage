package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskmanipulator.NetworkNodeDiskManipulator;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.tile.TileDiskManipulator;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideButtonIOMode extends SideButton {
    public SideButtonIOMode(BaseScreen gui) {
        super(gui);
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage:iomode") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage:iomode." + (TileDiskManipulator.IO_MODE.getValue() == NetworkNodeDiskManipulator.IO_MODE_INSERT ? "insert" : "extract"));
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.blit(x, y, TileDiskManipulator.IO_MODE.getValue() == NetworkNodeDiskManipulator.IO_MODE_EXTRACT ? 0 : 16, 160, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(TileDiskManipulator.IO_MODE, TileDiskManipulator.IO_MODE.getValue() == NetworkNodeDiskManipulator.IO_MODE_INSERT ? NetworkNodeDiskManipulator.IO_MODE_EXTRACT : NetworkNodeDiskManipulator.IO_MODE_INSERT);
    }
}
