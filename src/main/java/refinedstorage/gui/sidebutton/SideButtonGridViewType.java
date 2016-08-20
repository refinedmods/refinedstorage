package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;

public class SideButtonGridViewType extends SideButton {
    private IGrid grid;

    public SideButtonGridViewType(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.YELLOW + gui.t("sidebutton.refinedstorage:grid.view_type") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:grid.view_type." + grid.getViewType());
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");
        gui.drawTexture(x, y + 2 - 1, grid.getViewType() * 16, 112, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int type = grid.getViewType();

        if (type == TileGrid.VIEW_TYPE_NORMAL) {
            type = TileGrid.VIEW_TYPE_NON_CRAFTABLES;
        } else if (type == TileGrid.VIEW_TYPE_NON_CRAFTABLES) {
            type = TileGrid.VIEW_TYPE_CRAFTABLES;
        } else if (type == TileGrid.VIEW_TYPE_CRAFTABLES) {
            type = TileGrid.VIEW_TYPE_NORMAL;
        }

        grid.onViewTypeChanged(type);
    }
}
