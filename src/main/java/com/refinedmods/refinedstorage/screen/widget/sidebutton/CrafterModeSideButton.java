package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.container.CrafterContainer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.CrafterTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class CrafterModeSideButton extends SideButton {
    public CrafterModeSideButton(BaseScreen<CrafterContainer> screen) {
        super(screen);
    }

    @Override
    public String getTooltip() {
        return new TranslationTextComponent("sidebutton.refinedstorage.crafter_mode").getString() + "\n" + TextFormatting.GRAY + new TranslationTextComponent("sidebutton.refinedstorage.crafter_mode." + CrafterTile.MODE.getValue()).getString();
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, CrafterTile.MODE.getValue() * 16, 0, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(CrafterTile.MODE, CrafterTile.MODE.getValue() + 1);
    }
}
