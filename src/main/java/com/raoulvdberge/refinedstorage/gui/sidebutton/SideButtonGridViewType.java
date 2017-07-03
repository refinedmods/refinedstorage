package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import net.minecraft.util.text.TextFormatting;

public class SideButtonGridViewType extends SideButton {
    private IGrid grid;

    public SideButtonGridViewType(GuiBase gui, IGrid grid) {
        super(gui);

        this.grid = grid;
    }

    @Override
    public String getTooltip() {
        return GuiBase.t("sidebutton.refinedstorage:grid.view_type") + "\n" + TextFormatting.GRAY + GuiBase.t("sidebutton.refinedstorage:grid.view_type." + grid.getViewType());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, grid.getViewType() * 16, 112, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int type = grid.getViewType();

        if (type == NetworkNodeGrid.VIEW_TYPE_NORMAL) {
            type = NetworkNodeGrid.VIEW_TYPE_NON_CRAFTABLES;
        } else if (type == NetworkNodeGrid.VIEW_TYPE_NON_CRAFTABLES) {
            type = NetworkNodeGrid.VIEW_TYPE_CRAFTABLES;
        } else if (type == NetworkNodeGrid.VIEW_TYPE_CRAFTABLES) {
            type = NetworkNodeGrid.VIEW_TYPE_NORMAL;
        }

        grid.onViewTypeChanged(type);
    }
}
