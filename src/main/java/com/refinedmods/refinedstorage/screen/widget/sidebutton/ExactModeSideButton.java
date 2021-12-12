package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class ExactModeSideButton extends SideButton {
    private static final int MASK = IComparer.COMPARE_NBT;

    private final TileDataParameter<Integer, ?> parameter;

    public ExactModeSideButton(BaseScreen<?> screen, TileDataParameter<Integer, ?> parameter) {
        super(screen);

        this.parameter = parameter;
    }

    @Override
    public String getTooltip() {
        String tooltip = I18n.get("sidebutton.refinedstorage.exact_mode") + "\n" + ChatFormatting.GRAY;

        if ((parameter.getValue() & MASK) == MASK) {
            tooltip += I18n.get("sidebutton.refinedstorage.exact_mode.on");
        } else {
            tooltip += I18n.get("sidebutton.refinedstorage.exact_mode.off");
        }

        return tooltip;
    }

    @Override
    protected void renderButtonIcon(PoseStack matrixStack, int x, int y) {
        int ty = 16 * 12;
        int tx = (parameter.getValue() & MASK) == MASK ? 0 : 16;

        screen.blit(matrixStack, x, y, tx, ty, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(parameter, parameter.getValue() ^ MASK);
    }
}
