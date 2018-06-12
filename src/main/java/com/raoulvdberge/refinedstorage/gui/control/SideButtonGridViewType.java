package com.raoulvdberge.refinedstorage.gui.control;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
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
        gui.drawTexture(x, y, (grid.getViewType() - (grid.getViewType() >= 3 ? 3 : 0)) * 16, 112, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int type = grid.getViewType();

        if (type == IGrid.VIEW_TYPE_NORMAL) {
            type = IGrid.VIEW_TYPE_NON_CRAFTABLES;
        } else if (type == IGrid.VIEW_TYPE_NON_CRAFTABLES) {
            type = IGrid.VIEW_TYPE_CRAFTABLES;
        } else if (type == IGrid.VIEW_TYPE_CRAFTABLES) {
            type = IGrid.VIEW_TYPE_NORMAL;
        }

        grid.onViewTypeChanged(type);
    }
}
