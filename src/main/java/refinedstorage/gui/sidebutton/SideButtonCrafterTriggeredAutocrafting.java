package refinedstorage.gui.sidebutton;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.TileCrafter;
import refinedstorage.tile.data.TileDataManager;

public class SideButtonCrafterTriggeredAutocrafting extends SideButton {
    public SideButtonCrafterTriggeredAutocrafting(GuiBase gui) {
        super(gui);
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:crafter.triggered_autocrafting") + TextFormatting.RESET + "\n" + gui.t("gui." + (TileCrafter.TRIGGERED_AUTOCRAFTING.getValue() ? "yes" : "no"));
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);

        gui.drawTexture(xPosition, yPosition, 0, 144, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(TileCrafter.TRIGGERED_AUTOCRAFTING, !TileCrafter.TRIGGERED_AUTOCRAFTING.getValue());
    }
}
