package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.util.AccessTypeUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

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
        return I18n.get("sidebutton.refinedstorage.access_type") + "\n" + TextFormatting.GRAY + I18n.get("sidebutton.refinedstorage.access_type." + parameter.getValue().getId());
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(parameter, AccessTypeUtils.getAccessType(parameter.getValue().getId() + 1));
    }
}
