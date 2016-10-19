package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import net.minecraft.util.text.TextFormatting;

public class SideButtonGridSortingDirection extends SideButton {
    private IGrid grid;

    public SideButtonGridSortingDirection(GuiBase gui, IGrid grid) {
        super(gui);

        this.grid = grid;
    }

    @Override
    public String getTooltip() {
        return TextFormatting.YELLOW + GuiBase.t("sidebutton.refinedstorage:grid.sorting.direction") + TextFormatting.RESET + "\n" + GuiBase.t("sidebutton.refinedstorage:grid.sorting.direction." + grid.getSortingDirection());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, grid.getSortingDirection() * 16, 16, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int dir = grid.getSortingDirection();

        if (dir == TileGrid.SORTING_DIRECTION_ASCENDING) {
            dir = TileGrid.SORTING_DIRECTION_DESCENDING;
        } else if (dir == TileGrid.SORTING_DIRECTION_DESCENDING) {
            dir = TileGrid.SORTING_DIRECTION_ASCENDING;
        }

        grid.onSortingDirectionChanged(dir);
    }
}
