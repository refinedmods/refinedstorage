package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.screen.FilterScreen;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideButtonFilterType extends SideButton {
    private FilterScreen gui;

    public SideButtonFilterType(FilterScreen gui) {
        super(gui);

        this.gui = gui;
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage:type") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage:type." + gui.getType());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.blit(x, y, 16 * gui.getType(), 128, 16, 16);
    }

    @Override
    public void onPress() {
        gui.setType(gui.getType() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
        gui.sendUpdate();
    }
}
