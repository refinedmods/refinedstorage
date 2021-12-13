package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.container.DestructorContainer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.blockentity.DestructorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class DestructorPickupSideButton extends SideButton {
    public DestructorPickupSideButton(BaseScreen<DestructorContainer> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(PoseStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, 64 + (Boolean.TRUE.equals(DestructorBlockEntity.PICKUP.getValue()) ? 0 : 16), 0, 16, 16);
    }

    @Override
    protected String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.destructor.pickup") + "\n" + ChatFormatting.GRAY + I18n.get(Boolean.TRUE.equals(DestructorBlockEntity.PICKUP.getValue()) ? "gui.yes" : "gui.no");
    }

    @Override
    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(DestructorBlockEntity.PICKUP, !DestructorBlockEntity.PICKUP.getValue());
    }
}
