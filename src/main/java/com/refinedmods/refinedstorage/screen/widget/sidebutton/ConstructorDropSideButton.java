package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.container.ConstructorContainer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.ConstructorTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class ConstructorDropSideButton extends SideButton {
    public ConstructorDropSideButton(BaseScreen<ConstructorContainer> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(PoseStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, 64 + (Boolean.TRUE.equals(ConstructorTile.DROP.getValue()) ? 16 : 0), 16, 16, 16);
    }

    @Override
    public String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.constructor.drop") + "\n" + ChatFormatting.GRAY + I18n.get(Boolean.TRUE.equals(ConstructorTile.DROP.getValue()) ? "gui.yes" : "gui.no");
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(ConstructorTile.DROP, !ConstructorTile.DROP.getValue());
    }
}
