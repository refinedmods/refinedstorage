package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
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

        if (size == NetworkNodeGrid.SIZE_STRETCH) {
            tx = 48;
        } else if (size == NetworkNodeGrid.SIZE_SMALL) {
            tx = 0;
        } else if (size == NetworkNodeGrid.SIZE_MEDIUM) {
            tx = 16;
        } else if (size == NetworkNodeGrid.SIZE_LARGE) {
            tx = 32;
        }

        gui.drawTexture(x, y, 64 + tx, 64, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int size = grid.getSize();

        if (size == NetworkNodeGrid.SIZE_STRETCH) {
            size = NetworkNodeGrid.SIZE_SMALL;
        } else if (size == NetworkNodeGrid.SIZE_SMALL) {
            size = NetworkNodeGrid.SIZE_MEDIUM;
        } else if (size == NetworkNodeGrid.SIZE_MEDIUM) {
            size = NetworkNodeGrid.SIZE_LARGE;
        } else if (size == NetworkNodeGrid.SIZE_LARGE) {
            size = NetworkNodeGrid.SIZE_STRETCH;
        }

        grid.onSizeChanged(size);
    }
}
