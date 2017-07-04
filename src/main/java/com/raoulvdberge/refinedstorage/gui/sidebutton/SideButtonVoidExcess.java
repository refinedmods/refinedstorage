package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.text.TextFormatting;

public class SideButtonVoidExcess extends SideButton {
    private TileDataParameter<Boolean, ?> parameter;
    private String type;

    public SideButtonVoidExcess(GuiBase gui, TileDataParameter<Boolean, ?> parameter, String type) {
        super(gui);

        this.parameter = parameter;
        this.type = type;
    }

    @Override
    public String getTooltip() {
        return GuiBase.t("sidebutton.refinedstorage:void_excess." + type) + "\n" + TextFormatting.GRAY + GuiBase.t(parameter.getValue() ? "gui.yes" : "gui.no");
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, parameter.getValue() ? 16 : 0, 192, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, !parameter.getValue());
    }
}
