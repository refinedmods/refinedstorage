package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.TileCrafter;
import refinedstorage.tile.data.TileDataManager;

public class SideButtonCrafterTriggeredAutocrafting extends SideButton {
    public SideButtonCrafterTriggeredAutocrafting(GuiBase gui) {
        super(gui);
    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:crafter.triggered_autocrafting") + TextFormatting.RESET + "\n" + gui.t("gui." + (TileCrafter.TRIGGERED_AUTOCRAFTING.getValue() ? "yes" : "no"));
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, TileCrafter.TRIGGERED_AUTOCRAFTING.getValue() ? 0 : 16, 144, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileCrafter.TRIGGERED_AUTOCRAFTING, !TileCrafter.TRIGGERED_AUTOCRAFTING.getValue());
    }
}
