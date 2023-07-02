package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.container.DestructorContainerMenu;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.blockentity.DestructorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class DestructorPickupSideButton extends SideButton {
    public DestructorPickupSideButton(BaseScreen<DestructorContainerMenu> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, 64 + (Boolean.TRUE.equals(DestructorBlockEntity.PICKUP.getValue()) ? 0 : 16), 0, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.refinedstorage.destructor.pickup") + "\n" + ChatFormatting.GRAY + I18n.get(Boolean.TRUE.equals(DestructorBlockEntity.PICKUP.getValue()) ? "gui.yes" : "gui.no");
    }

    @Override
    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(DestructorBlockEntity.PICKUP, !DestructorBlockEntity.PICKUP.getValue());
    }
}
