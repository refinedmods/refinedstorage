package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.TileDiskManipulator;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.util.text.TextFormatting;

public class SideButtonIOMode extends SideButton {
    public SideButtonIOMode(GuiBase gui) {
        super(gui);
    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + GuiBase.t("sidebutton.refinedstorage:iomode") + TextFormatting.RESET + "\n" + GuiBase.t("sidebutton.refinedstorage:iomode." + (TileDiskManipulator.IO_MODE.getValue() == TileDiskManipulator.IO_MODE_INSERT ? "insert" : "extract"));
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, TileDiskManipulator.IO_MODE.getValue() == TileDiskManipulator.IO_MODE_EXTRACT ? 0 : 16, 160, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileDiskManipulator.IO_MODE, TileDiskManipulator.IO_MODE.getValue() == TileDiskManipulator.IO_MODE_INSERT ? TileDiskManipulator.IO_MODE_EXTRACT : TileDiskManipulator.IO_MODE_INSERT);
    }
}
