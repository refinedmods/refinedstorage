package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.TileDestructor;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.util.text.TextFormatting;

public class SideButtonPickup extends SideButton {
    public SideButtonPickup(GuiBase gui) {
        super(gui);
    }

    @Override
    protected void drawButtonIcon(int x, int y) {

    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + GuiBase.t("sidebutton.refinedstorage:destructor.pickup") + TextFormatting.RESET + "\n" + GuiBase.t(TileDestructor.PICKUP.getValue() ? "gui.yes" : "gui.no");
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileDestructor.PICKUP, !TileDestructor.PICKUP.getValue());
    }
}
