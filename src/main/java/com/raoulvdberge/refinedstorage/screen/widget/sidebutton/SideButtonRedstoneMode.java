package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideButtonRedstoneMode extends SideButton {
    private TileDataParameter<Integer, ?> parameter;

    public SideButtonRedstoneMode(BaseScreen gui, TileDataParameter<Integer, ?> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage:redstone_mode") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage:redstone_mode." + parameter.getValue());
    }

    @Override
    protected void renderButtonIcon(int x, int y) {
        screen.blit(x, y, parameter.getValue() * 16, 0, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(parameter, parameter.getValue() + 1);
    }
}
