package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.util.text.TextFormatting;

public class SideButtonExporterRegulator extends SideButton {
    public SideButtonExporterRegulator(GuiBase gui) {
        super(gui);
    }

    @Override
    protected void drawButtonIcon(int x, int y) {

    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + GuiBase.t("sidebutton.refinedstorage:exporter.regulator") + TextFormatting.RESET + "\n" + GuiBase.t(TileExporter.REGULATOR.getValue() ? "gui.yes" : "gui.no");
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileExporter.REGULATOR, !TileExporter.REGULATOR.getValue());
    }
}
