package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.TileDestructor;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.util.text.TextFormatting;

public class SideButtonDestructorPickup extends SideButton {
    public SideButtonDestructorPickup(GuiBase gui) {
        super(gui);
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, 64 + (!TileDestructor.PICKUP.getValue() ? 16 : 0), 0, 16, 16);
    }

    @Override
    public String getTooltip() {
        return GuiBase.t("sidebutton.refinedstorage:destructor.pickup") + "\n" + TextFormatting.GRAY + GuiBase.t(TileDestructor.PICKUP.getValue() ? "gui.yes" : "gui.no");
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileDestructor.PICKUP, !TileDestructor.PICKUP.getValue());
    }
}
