package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.blockentity.ConstructorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.container.ConstructorContainerMenu;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class ConstructorDropSideButton extends SideButton {
    public ConstructorDropSideButton(BaseScreen<ConstructorContainerMenu> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, 64 + (Boolean.TRUE.equals(ConstructorBlockEntity.DROP.getValue()) ? 16 : 0), 16, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.refinedstorage.constructor.drop") + "\n" + ChatFormatting.GRAY + I18n.get(Boolean.TRUE.equals(ConstructorBlockEntity.DROP.getValue()) ? "gui.yes" : "gui.no");
    }

    @Override
    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(ConstructorBlockEntity.DROP, !ConstructorBlockEntity.DROP.getValue());
    }
}
