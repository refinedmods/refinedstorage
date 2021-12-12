package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class WhitelistBlacklistSideButton extends SideButton {
    private final TileDataParameter<Integer, ?> parameter;

    public WhitelistBlacklistSideButton(BaseScreen<?> screen, TileDataParameter<Integer, ?> parameter) {
        super(screen);

        this.parameter = parameter;
    }

    @Override
    protected String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.mode") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.mode." + (parameter.getValue() == IWhitelistBlacklist.WHITELIST ? "whitelist" : "blacklist"));
    }

    @Override
    protected void renderButtonIcon(PoseStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, parameter.getValue() == IWhitelistBlacklist.WHITELIST ? 0 : 16, 64, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(parameter, parameter.getValue() == IWhitelistBlacklist.WHITELIST ? IWhitelistBlacklist.BLACKLIST : IWhitelistBlacklist.WHITELIST);
    }
}
