package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;

public class SideButtonGridSearchBoxMode extends SideButton {
    private IGrid grid;

    public SideButtonGridSearchBoxMode(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.YELLOW + gui.t("sidebutton.refinedstorage:grid.search_box_mode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:grid.search_box_mode." + grid.getSearchBoxMode());
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

        grid.onSearchBoxModeChanged(mode);
    }
}
