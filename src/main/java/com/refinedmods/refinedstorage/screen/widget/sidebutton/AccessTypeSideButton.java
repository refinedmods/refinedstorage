package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.util.AccessTypeUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class AccessTypeSideButton extends SideButton {
    private final TileDataParameter<AccessType, ?> parameter;

    public AccessTypeSideButton(BaseScreen<?> screen, TileDataParameter<AccessType, ?> parameter) {
        super(screen);

        this.parameter = parameter;
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, 16 * parameter.getValue().getId(), 240, 16, 16);
    }

    @Override
    public String getTooltip() {
        return new TranslationTextComponent("sidebutton.refinedstorage.access_type").getString() + "\n" + TextFormatting.GRAY + new TranslationTextComponent("sidebutton.refinedstorage.access_type." + parameter.getValue().getId()).getString();
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(parameter, AccessTypeUtils.getAccessType(parameter.getValue().getId() + 1));
    }
}
