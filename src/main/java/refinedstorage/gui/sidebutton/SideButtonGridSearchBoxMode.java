package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.gui.GuiBase;
import refinedstorage.network.MessageGridSettingsUpdate;
import refinedstorage.tile.TileGrid;

public class SideButtonGridSearchBoxMode extends SideButton {
    private TileGrid grid;

    public SideButtonGridSearchBoxMode(TileGrid grid) {
        this.grid = grid;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        StringBuilder builder = new StringBuilder();

        builder.append(TextFormatting.YELLOW).append(gui.t("sidebutton.refinedstorage:grid.search_box_mode")).append(TextFormatting.RESET).append("\n");
        builder.append(gui.t("sidebutton.refinedstorage:grid.search_box_mode." + grid.getSearchBoxMode()));

        return builder.toString();
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");
        gui.drawTexture(x, y + 2 - 1, 0, 96, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int mode = grid.getSearchBoxMode();

        if (mode == TileGrid.SEARCH_BOX_MODE_NORMAL) {
            mode = TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED;
        } else if (mode == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED) {
            mode = TileGrid.SEARCH_BOX_MODE_NORMAL;
        }

        RefinedStorage.NETWORK.sendToServer(new MessageGridSettingsUpdate(grid, grid.getSortingDirection(), grid.getSortingType(), mode));
    }
}
