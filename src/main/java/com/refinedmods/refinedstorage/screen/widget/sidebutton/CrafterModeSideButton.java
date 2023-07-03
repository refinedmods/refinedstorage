package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.blockentity.CrafterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.container.CrafterContainerMenu;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class CrafterModeSideButton extends SideButton {
    public CrafterModeSideButton(BaseScreen<CrafterContainerMenu> screen) {
        super(screen);
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.refinedstorage.crafter_mode") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.crafter_mode." + CrafterBlockEntity.MODE.getValue());
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, CrafterBlockEntity.MODE.getValue() * 16, 0, 16, 16);
    }

    @Override
    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(CrafterBlockEntity.MODE, CrafterBlockEntity.MODE.getValue() + 1);
    }
}
