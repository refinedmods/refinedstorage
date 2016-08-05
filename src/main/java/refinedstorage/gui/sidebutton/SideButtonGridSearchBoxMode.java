package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.gui.grid.GuiGrid;
import refinedstorage.integration.jei.IntegrationJEI;
import refinedstorage.tile.grid.TileGrid;

public class SideButtonGridSearchBoxMode extends SideButton {
    private GuiGrid gui;

    public SideButtonGridSearchBoxMode(GuiGrid gui) {
        this.gui = gui;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.YELLOW + gui.t("sidebutton.refinedstorage:grid.search_box_mode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:grid.search_box_mode." + this.gui.getGrid().getSearchBoxMode());
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");
        gui.drawTexture(x, y + 2 - 1, 0, 96, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int mode = gui.getGrid().getSearchBoxMode();

        if (mode == TileGrid.SEARCH_BOX_MODE_NORMAL) {
            mode = TileGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED;
        } else if (mode == TileGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED) {
            if (IntegrationJEI.isLoaded()) {
                mode = TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED;
            } else {
                mode = TileGrid.SEARCH_BOX_MODE_NORMAL;
            }
        } else if (mode == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED) {
            mode = TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED;
        } else if (mode == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED) {
            mode = TileGrid.SEARCH_BOX_MODE_NORMAL;
        }

        gui.getGrid().onSearchBoxModeChanged(mode);
    }
}
