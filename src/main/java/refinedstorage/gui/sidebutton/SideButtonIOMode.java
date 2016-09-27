package refinedstorage.gui.sidebutton;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.TileDiskManipulator;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonIOMode extends SideButton {
    private TileDataParameter<Integer> parameter;

    public SideButtonIOMode(GuiBase gui, TileDataParameter<Integer> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:iomode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:iomode." + (parameter.getValue() == TileDiskManipulator.IO_MODE_INSERT ? "insert" : "extract"));
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y) {
        super.drawButton(mc, x, y);

        gui.drawTexture(xPosition, yPosition, parameter.getValue() == TileDiskManipulator.IO_MODE_EXTRACT ? 0 : 16, 160, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() == TileDiskManipulator.IO_MODE_INSERT ? TileDiskManipulator.IO_MODE_EXTRACT : TileDiskManipulator.IO_MODE_INSERT);
    }
}
