package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class TypeSideButton extends SideButton {
    private final BlockEntitySynchronizationParameter<Integer, ?> type;

    public TypeSideButton(BaseScreen<?> screen, BlockEntitySynchronizationParameter<Integer, ?> type) {
        super(screen);

        this.type = type;
    }

    @Override
    protected String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.type") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.type." + type.getValue());
    }

    @Override
    protected void renderButtonIcon(PoseStack poseStack, int x, int y) {
        screen.blit(poseStack, x, y, 16 * type.getValue(), 128, 16, 16);
    }

    @Override
    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(type, type.getValue() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
    }
}
