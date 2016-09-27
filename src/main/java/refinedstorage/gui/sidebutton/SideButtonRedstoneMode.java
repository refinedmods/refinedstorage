package refinedstorage.gui.sidebutton;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonRedstoneMode extends SideButton {
    private TileDataParameter<Integer> parameter;

    public SideButtonRedstoneMode(GuiBase gui, TileDataParameter<Integer> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.RED + gui.t("sidebutton.refinedstorage:redstone_mode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:redstone_mode." + parameter.getValue());
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y) {
        super.drawButton(mc, x, y);

        gui.drawTexture(xPosition, yPosition, parameter.getValue() * 16, 0, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() + 1);
    }
}
