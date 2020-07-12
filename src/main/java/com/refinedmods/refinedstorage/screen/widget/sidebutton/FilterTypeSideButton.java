package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.FilterScreen;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class FilterTypeSideButton extends SideButton {
    private final FilterScreen screen;

    public FilterTypeSideButton(FilterScreen screen) {
        super(screen);

        this.screen = screen;
    }

    @Override
    public String getTooltip() {
        return new TranslationTextComponent("sidebutton.refinedstorage.type").getString() + "\n" + TextFormatting.GRAY + new TranslationTextComponent("sidebutton.refinedstorage.type." + screen.getType()).getString();
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        screen.blit(matrixStack,x, y, 16 * screen.getType(), 128, 16, 16);
    }

    @Override
    public void onPress() {
        screen.setType(screen.getType() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
        screen.sendUpdate();
    }
}
