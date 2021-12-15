package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.container.CrafterContainerMenu;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.blockentity.CrafterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class CrafterModeSideButton extends SideButton {
    public CrafterModeSideButton(BaseScreen<CrafterContainerMenu> screen) {
        super(screen);
    }

    @Override
    protected String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.crafter_mode") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.crafter_mode." + CrafterBlockEntity.MODE.getValue());
    }

    @Override
    protected void renderButtonIcon(PoseStack poseStack, int x, int y) {
        screen.blit(poseStack, x, y, CrafterBlockEntity.MODE.getValue() * 16, 0, 16, 16);
    }

    @Override
    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(CrafterBlockEntity.MODE, CrafterBlockEntity.MODE.getValue() + 1);
    }
}
