package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.FilterScreen;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class FilterTypeSideButton extends SideButton {
    private final FilterScreen filterScreen;

    public FilterTypeSideButton(FilterScreen filterScreen) {
        super(filterScreen);

        this.filterScreen = filterScreen;
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.refinedstorage.type") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.type." + filterScreen.getType());
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, 16 * filterScreen.getType(), 128, 16, 16);
    }

    @Override
    public void onPress() {
        filterScreen.setType(filterScreen.getType() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
        filterScreen.sendUpdate();
    }
}
