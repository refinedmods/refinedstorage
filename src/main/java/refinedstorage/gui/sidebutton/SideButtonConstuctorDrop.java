package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.TileConstructor;
import refinedstorage.tile.data.TileDataManager;

public class SideButtonConstuctorDrop extends SideButton {
    public SideButtonConstuctorDrop(GuiBase gui) {
        super(gui);
    }

    @Override
    protected void drawButtonIcon(int x, int y) {

    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + GuiBase.t("sidebutton.refinedstorage:constructor.drop") + TextFormatting.RESET + "\n" + GuiBase.t(TileConstructor.DROP.getValue() ? "gui.yes" : "gui.no");
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileConstructor.DROP, !TileConstructor.DROP.getValue());
    }
}
