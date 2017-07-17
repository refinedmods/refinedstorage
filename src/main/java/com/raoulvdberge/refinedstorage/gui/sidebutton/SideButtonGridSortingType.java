package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import net.minecraft.util.text.TextFormatting;

public class SideButtonGridSortingType extends SideButton {
    private IGrid grid;

    public SideButtonGridSortingType(GuiBase gui, IGrid grid) {
        super(gui);

        this.grid = grid;
    }

    @Override
    public String getTooltip() {
        return GuiBase.t("sidebutton.refinedstorage:grid.sorting.type") + "\n" + TextFormatting.GRAY + GuiBase.t("sidebutton.refinedstorage:grid.sorting.type." + grid.getSortingType());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, grid.getSortingType() * 16, 32, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int type = grid.getSortingType();

        if (type == NetworkNodeGrid.SORTING_TYPE_QUANTITY) {
            type = NetworkNodeGrid.SORTING_TYPE_NAME;
        } else if (type == NetworkNodeGrid.SORTING_TYPE_NAME) {
            if (grid.getType() == GridType.FLUID) {
                type = NetworkNodeGrid.SORTING_TYPE_QUANTITY;
            } else {
                type = NetworkNodeGrid.SORTING_TYPE_ID;
            }
        } else if (type == NetworkNodeGrid.SORTING_TYPE_ID) {
            type = NetworkNodeGrid.SORTING_TYPE_QUANTITY;
        }

        grid.onSortingTypeChanged(type);
    }
}
