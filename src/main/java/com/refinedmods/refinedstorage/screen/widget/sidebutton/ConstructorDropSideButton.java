package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.container.ConstructorContainer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.ConstructorTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ConstructorDropSideButton extends SideButton {
    public ConstructorDropSideButton(BaseScreen<ConstructorContainer> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, 64 + (ConstructorTile.DROP.getValue() ? 16 : 0), 16, 16, 16);
    }

    @Override
    public String getTooltip() {
        return new TranslationTextComponent("sidebutton.refinedstorage.constructor.drop").getString() + "\n" + TextFormatting.GRAY + new TranslationTextComponent(ConstructorTile.DROP.getValue() ? "gui.yes" : "gui.no").getString();
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(ConstructorTile.DROP, !ConstructorTile.DROP.getValue());
    }
}
