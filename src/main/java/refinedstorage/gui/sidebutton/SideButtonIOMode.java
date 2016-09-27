package refinedstorage.gui.sidebutton;

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
    public String getTooltip() {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:iomode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:iomode." + (parameter.getValue() == TileDiskManipulator.IO_MODE_INSERT ? "insert" : "extract"));
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, parameter.getValue() == TileDiskManipulator.IO_MODE_EXTRACT ? 0 : 16, 160, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() == TileDiskManipulator.IO_MODE_INSERT ? TileDiskManipulator.IO_MODE_EXTRACT : TileDiskManipulator.IO_MODE_INSERT);
    }
}
