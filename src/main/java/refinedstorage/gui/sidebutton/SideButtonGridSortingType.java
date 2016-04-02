package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;

public class SideButtonGridSortingType extends SideButton {
    private IGrid grid;

    public SideButtonGridSortingType(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.YELLOW + gui.t("sidebutton.refinedstorage:grid.sorting.type") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:grid.sorting.type." + grid.getSortingType());
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");
        gui.drawTexture(x - 1, y + 2 - 1, grid.getSortingType() * 16, 32, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int type = grid.getSortingType();

        if (type == TileGrid.SORTING_TYPE_QUANTITY) {
            type = TileGrid.SORTING_TYPE_NAME;
        } else if (type == TileGrid.SORTING_TYPE_NAME) {
            type = TileGrid.SORTING_TYPE_QUANTITY;
        }

        grid.onSortingTypeChanged(type);
    }
}
