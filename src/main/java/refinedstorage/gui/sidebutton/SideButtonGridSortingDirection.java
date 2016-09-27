package refinedstorage.gui.sidebutton;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;

public class SideButtonGridSortingDirection extends SideButton {
    private IGrid grid;

    public SideButtonGridSortingDirection(GuiBase gui, IGrid grid) {
        super(gui);

        this.grid = grid;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.YELLOW + gui.t("sidebutton.refinedstorage:grid.sorting.direction") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:grid.sorting.direction." + grid.getSortingDirection());
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y) {
        super.drawButton(mc, x, y);

        gui.drawTexture(xPosition, yPosition, grid.getSortingDirection() * 16, 16, 16, 16);
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
