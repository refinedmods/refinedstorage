package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class RedstoneModeSideButton extends SideButton {
    private final BlockEntitySynchronizationParameter<Integer, ?> parameter;

    public RedstoneModeSideButton(BaseScreen<?> screen, BlockEntitySynchronizationParameter<Integer, ?> parameter) {
        super(screen);

        this.parameter = parameter;
    }

    @Override
    protected String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.redstone_mode") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.redstone_mode." + parameter.getValue());
    }

    @Override
    protected void renderButtonIcon(PoseStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, parameter.getValue() * 16, 0, 16, 16);
    }

    @Override
    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(parameter, parameter.getValue() + 1);
    }
}
