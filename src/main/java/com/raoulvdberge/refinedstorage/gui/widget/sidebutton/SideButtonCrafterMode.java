package com.raoulvdberge.refinedstorage.gui.widget.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.TileCrafter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideButtonCrafterMode extends SideButton {
    public SideButtonCrafterMode(GuiBase gui) {
        super(gui);
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage:crafter_mode") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage:crafter_mode." + TileCrafter.MODE.getValue());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.blit(x, y, TileCrafter.MODE.getValue() * 16, 0, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(TileCrafter.MODE, TileCrafter.MODE.getValue() + 1);
    }
}
