package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.util.text.TextFormatting;

public class SideButtonExporterCraftOnly extends SideButton {
    public SideButtonExporterCraftOnly(GuiBase gui) {
        super(gui);
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, 64 + (TileExporter.CRAFT_ONLY.getValue() ? 16 : 0), 32, 16, 16);
    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + GuiBase.t("sidebutton.refinedstorage:exporter.craft_only") + TextFormatting.RESET + "\n" + GuiBase.t(TileExporter.CRAFT_ONLY.getValue() ? "gui.yes" : "gui.no");
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileExporter.CRAFT_ONLY, !TileExporter.CRAFT_ONLY.getValue());
    }
}
