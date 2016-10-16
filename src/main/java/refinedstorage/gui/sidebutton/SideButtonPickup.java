package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.TileDestructor;
import refinedstorage.tile.data.TileDataManager;

public class SideButtonPickup extends SideButton {
    public SideButtonPickup(GuiBase gui) {
        super(gui);
    }

    @Override
    protected void drawButtonIcon(int x, int y) {

    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + GuiBase.t("sidebutton.refinedstorage:destructor.pickup") + TextFormatting.RESET + "\n" + GuiBase.t(TileDestructor.PICKUP.getValue() ? "gui.yes" : "gui.no");
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileDestructor.PICKUP, !TileDestructor.PICKUP.getValue());
    }
}
