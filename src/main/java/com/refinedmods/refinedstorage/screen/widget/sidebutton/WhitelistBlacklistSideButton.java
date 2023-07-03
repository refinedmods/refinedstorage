package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class WhitelistBlacklistSideButton extends SideButton {
    private final BlockEntitySynchronizationParameter<Integer, ?> parameter;

    public WhitelistBlacklistSideButton(BaseScreen<?> screen, BlockEntitySynchronizationParameter<Integer, ?> parameter) {
        super(screen);

        this.parameter = parameter;
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.refinedstorage.mode") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.mode." + (parameter.getValue() == IWhitelistBlacklist.WHITELIST ? "whitelist" : "blacklist"));
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, parameter.getValue() == IWhitelistBlacklist.WHITELIST ? 0 : 16, 64, 16, 16);
    }

    @Override
    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(parameter, parameter.getValue() == IWhitelistBlacklist.WHITELIST ? IWhitelistBlacklist.BLACKLIST : IWhitelistBlacklist.WHITELIST);
    }
}
