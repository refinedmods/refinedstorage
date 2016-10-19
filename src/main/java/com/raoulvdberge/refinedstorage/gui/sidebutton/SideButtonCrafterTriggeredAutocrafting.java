package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.TileCrafter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.util.text.TextFormatting;

public class SideButtonCrafterTriggeredAutocrafting extends SideButton {
    public SideButtonCrafterTriggeredAutocrafting(GuiBase gui) {
        super(gui);
    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + GuiBase.t("sidebutton.refinedstorage:crafter.triggered_autocrafting") + TextFormatting.RESET + "\n" + GuiBase.t("gui." + (TileCrafter.TRIGGERED_AUTOCRAFTING.getValue() ? "yes" : "no"));
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, TileCrafter.TRIGGERED_AUTOCRAFTING.getValue() ? 0 : 16, 144, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileCrafter.TRIGGERED_AUTOCRAFTING, !TileCrafter.TRIGGERED_AUTOCRAFTING.getValue());
    }
}
