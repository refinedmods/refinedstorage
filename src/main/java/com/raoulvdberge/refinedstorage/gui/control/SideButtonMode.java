package com.raoulvdberge.refinedstorage.gui.control;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.text.TextFormatting;

public class SideButtonMode extends SideButton {
    private TileDataParameter<Integer, ?> parameter;

    public SideButtonMode(GuiBase gui, TileDataParameter<Integer, ?> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    public String getTooltip() {
        return GuiBase.t("sidebutton.refinedstorage:mode") + "\n" + TextFormatting.GRAY + GuiBase.t("sidebutton.refinedstorage:mode." + (parameter.getValue() == IFilterable.WHITELIST ? "whitelist" : "blacklist"));
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, parameter.getValue() == IFilterable.WHITELIST ? 0 : 16, 64, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() == IFilterable.WHITELIST ? IFilterable.BLACKLIST : IFilterable.WHITELIST);
    }
}
