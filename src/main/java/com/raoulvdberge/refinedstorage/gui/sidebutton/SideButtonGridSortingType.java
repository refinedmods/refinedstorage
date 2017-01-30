package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import net.minecraft.util.text.TextFormatting;

public class SideButtonGridSortingType extends SideButton {
    private IGrid grid;

    public SideButtonGridSortingType(GuiBase gui, IGrid grid) {
        super(gui);

        this.grid = grid;
    }

    @Override
    public String getTooltip() {
        return TextFormatting.YELLOW + GuiBase.t("sidebutton.refinedstorage:grid.sorting.type") + TextFormatting.RESET + "\n" + GuiBase.t("sidebutton.refinedstorage:grid.sorting.type." + grid.getSortingType());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, grid.getSortingType() * 16, 32, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int type = grid.getSortingType();

        if (type == TileGrid.SORTING_TYPE_QUANTITY) {
            type = TileGrid.SORTING_TYPE_NAME;
        } else if (type == TileGrid.SORTING_TYPE_NAME) {
            if (grid.getType() == EnumGridType.FLUID) {
                type = TileGrid.SORTING_TYPE_QUANTITY;
            } else {
                type = TileGrid.SORTING_TYPE_ID;
            }
        } else if (type == TileGrid.SORTING_TYPE_ID) {
            type = TileGrid.SORTING_TYPE_QUANTITY;
        }

        grid.onSortingTypeChanged(type);
    }
}
