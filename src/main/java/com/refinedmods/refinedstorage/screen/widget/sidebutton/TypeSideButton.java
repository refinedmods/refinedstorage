package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class TypeSideButton extends SideButton {
    private final TileDataParameter<Integer, ?> type;

    public TypeSideButton(BaseScreen<?> screen, TileDataParameter<Integer, ?> type) {
        super(screen);

        this.type = type;
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage.type") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.type." + type.getValue());
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, 16 * type.getValue(), 128, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(type, type.getValue() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
    }
}
