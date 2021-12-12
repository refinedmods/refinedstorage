package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.FilterScreen;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class FilterTypeSideButton extends SideButton {
    private final FilterScreen filterScreen;

    public FilterTypeSideButton(FilterScreen filterScreen) {
        super(filterScreen);

        this.filterScreen = filterScreen;
    }

    @Override
    public String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.type") + "\n" + TextFormatting.GRAY + I18n.get("sidebutton.refinedstorage.type." + filterScreen.getType());
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        filterScreen.blit(matrixStack, x, y, 16 * filterScreen.getType(), 128, 16, 16);
    }

    @Override
    public void onPress() {
        filterScreen.setType(filterScreen.getType() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
        filterScreen.sendUpdate();
    }
}
