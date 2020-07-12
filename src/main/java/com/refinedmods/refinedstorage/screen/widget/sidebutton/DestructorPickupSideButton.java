package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.container.DestructorContainer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.DestructorTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class DestructorPickupSideButton extends SideButton {
    public DestructorPickupSideButton(BaseScreen<DestructorContainer> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, 64 + (!DestructorTile.PICKUP.getValue() ? 16 : 0), 0, 16, 16);
    }

    @Override
    public String getTooltip() {
        return new TranslationTextComponent("sidebutton.refinedstorage.destructor.pickup").getString() + "\n" + TextFormatting.GRAY + new TranslationTextComponent(DestructorTile.PICKUP.getValue() ? "gui.yes" : "gui.no").getString();
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(DestructorTile.PICKUP, !DestructorTile.PICKUP.getValue());
    }
}
