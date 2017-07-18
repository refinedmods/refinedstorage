package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import net.minecraft.util.text.TextFormatting;

public class SideButtonGridSize extends SideButton {
    private IGrid grid;

    public SideButtonGridSize(GuiBase gui, IGrid grid) {
        super(gui);

        this.grid = grid;
    }

    @Override
    public String getTooltip() {
        return GuiBase.t("sidebutton.refinedstorage:grid.size") + "\n" + TextFormatting.GRAY + GuiBase.t("sidebutton.refinedstorage:grid.size." + grid.getSize());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        int size = grid.getSize();

        int tx = 0;

        if (size == IGrid.SIZE_STRETCH) {
            tx = 48;
        } else if (size == IGrid.SIZE_SMALL) {
            tx = 0;
        } else if (size == IGrid.SIZE_MEDIUM) {
            tx = 16;
        } else if (size == IGrid.SIZE_LARGE) {
            tx = 32;
        }

        gui.drawTexture(x, y, 64 + tx, 64, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int size = grid.getSize();

        if (size == IGrid.SIZE_STRETCH) {
            size = IGrid.SIZE_SMALL;
        } else if (size == IGrid.SIZE_SMALL) {
            size = IGrid.SIZE_MEDIUM;
        } else if (size == IGrid.SIZE_MEDIUM) {
            size = IGrid.SIZE_LARGE;
        } else if (size == IGrid.SIZE_LARGE) {
            size = IGrid.SIZE_STRETCH;
        }

        grid.onSizeChanged(size);
    }
}
