package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.TileCrafter;
import refinedstorage.tile.data.TileDataManager;

public class SideButtonCrafterTriggeredAutocrafting extends SideButton {
    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:crafter.triggered_autocrafting") + TextFormatting.RESET + "\n" + gui.t("gui." + (TileCrafter.TRIGGERED_AUTOCRAFTING.getValue() ? "yes" : "no"));
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");
        gui.drawTexture(x, y + 2 - 1, 0, 144, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileCrafter.TRIGGERED_AUTOCRAFTING, !TileCrafter.TRIGGERED_AUTOCRAFTING.getValue());
    }
}
