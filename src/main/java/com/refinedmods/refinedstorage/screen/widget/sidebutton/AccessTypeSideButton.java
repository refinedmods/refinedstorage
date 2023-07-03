package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.AccessTypeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class AccessTypeSideButton extends SideButton {
    private final BlockEntitySynchronizationParameter<AccessType, ?> parameter;

    public AccessTypeSideButton(BaseScreen<?> screen, BlockEntitySynchronizationParameter<AccessType, ?> parameter) {
        super(screen);

        this.parameter = parameter;
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, 16 * parameter.getValue().getId(), 240, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.refinedstorage.access_type") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.access_type." + parameter.getValue().getId());
    }

    @Override
    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(parameter, AccessTypeUtils.getAccessType(parameter.getValue().getId() + 1));
    }
}
