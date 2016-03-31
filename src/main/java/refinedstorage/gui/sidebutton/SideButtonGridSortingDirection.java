package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;

public class SideButtonGridSortingDirection extends SideButton {
    private IGrid grid;

    public SideButtonGridSortingDirection(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        StringBuilder builder = new StringBuilder();

        builder.append(TextFormatting.YELLOW).append(gui.t("sidebutton.refinedstorage:grid.sorting.direction")).append(TextFormatting.RESET).append("\n");
        builder.append(gui.t("sidebutton.refinedstorage:grid.sorting.direction." + grid.getSortingDirection()));

        return builder.toString();
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");
        gui.drawTexture(x, y + 2 - 1, grid.getSortingDirection() * 16, 16, 16, 16);
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
