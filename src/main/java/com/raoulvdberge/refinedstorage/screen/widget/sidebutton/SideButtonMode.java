package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.tile.config.IWhitelistBlacklist;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideButtonMode extends SideButton {
    private TileDataParameter<Integer, ?> parameter;

    public SideButtonMode(BaseScreen gui, TileDataParameter<Integer, ?> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage.mode") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.mode." + (parameter.getValue() == IWhitelistBlacklist.WHITELIST ? "whitelist" : "blacklist"));
    }

    @Override
    protected void renderButtonIcon(int x, int y) {
        screen.blit(x, y, parameter.getValue() == IWhitelistBlacklist.WHITELIST ? 0 : 16, 64, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(parameter, parameter.getValue() == IWhitelistBlacklist.WHITELIST ? IWhitelistBlacklist.BLACKLIST : IWhitelistBlacklist.WHITELIST);
    }
}
