package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.text.TextFormatting;

public class SideButtonType extends SideButton {
    private TileDataParameter<Integer> type;

    public SideButtonType(GuiBase gui, TileDataParameter<Integer> type) {
        super(gui);

        this.type = type;
    }

    @Override
    public String getTooltip() {
        return GuiBase.t("sidebutton.refinedstorage:type") + "\n" + TextFormatting.GRAY + GuiBase.t("sidebutton.refinedstorage:type." + type.getValue());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, 16 * type.getValue(), 128, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(type, type.getValue() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
    }
}
