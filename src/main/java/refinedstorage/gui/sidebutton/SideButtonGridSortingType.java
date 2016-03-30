package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.gui.GuiBase;
import refinedstorage.network.MessageGridSettingsUpdate;
import refinedstorage.tile.TileGrid;

public class SideButtonGridSortingType extends SideButton {
    private TileGrid grid;

    public SideButtonGridSortingType(TileGrid grid) {
        this.grid = grid;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        StringBuilder builder = new StringBuilder();

        builder.append(TextFormatting.YELLOW).append(gui.t("sidebutton.refinedstorage:grid.sorting.type")).append(TextFormatting.RESET).append("\n");
        builder.append(gui.t("sidebutton.refinedstorage:grid.sorting.type." + grid.getSortingType()));

        return builder.toString();
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

        RefinedStorage.NETWORK.sendToServer(new MessageGridSettingsUpdate(grid, grid.getSortingDirection(), type, grid.getSearchBoxMode()));
    }
}
