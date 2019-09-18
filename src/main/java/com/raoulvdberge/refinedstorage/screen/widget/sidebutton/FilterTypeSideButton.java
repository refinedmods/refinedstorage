package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.screen.FilterScreen;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class FilterTypeSideButton extends SideButton {
    private FilterScreen screen;

    public FilterTypeSideButton(FilterScreen screen) {
        super(screen);

        this.screen = screen;
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage:type") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage:type." + screen.getType());
    }

    @Override
    protected void renderButtonIcon(int x, int y) {
        screen.blit(x, y, 16 * screen.getType(), 128, 16, 16);
    }

    @Override
    public void onPress() {
        screen.setType(screen.getType() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
        screen.sendUpdate();
    }
}
