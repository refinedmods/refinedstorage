package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideButtonType extends SideButton {
    private TileDataParameter<Integer, ?> type;

    public SideButtonType(BaseScreen gui, TileDataParameter<Integer, ?> type) {
        super(gui);

        this.type = type;
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage:type") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage:type." + type.getValue());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.blit(x, y, 16 * type.getValue(), 128, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(type, type.getValue() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
    }
}
