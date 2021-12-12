package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.container.DestructorContainer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.DestructorTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class DestructorPickupSideButton extends SideButton {
    public DestructorPickupSideButton(BaseScreen<DestructorContainer> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, 64 + (Boolean.TRUE.equals(DestructorTile.PICKUP.getValue()) ? 0 : 16), 0, 16, 16);
    }

    @Override
    public String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.destructor.pickup") + "\n" + TextFormatting.GRAY + I18n.get(Boolean.TRUE.equals(DestructorTile.PICKUP.getValue()) ? "gui.yes" : "gui.no");
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(DestructorTile.PICKUP, !DestructorTile.PICKUP.getValue());
    }
}
