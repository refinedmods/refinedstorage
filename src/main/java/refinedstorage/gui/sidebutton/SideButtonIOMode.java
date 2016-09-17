package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.TileDiskManipulator;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonIOMode extends SideButton {
    private TileDataParameter<Integer> parameter;

    public SideButtonIOMode(TileDataParameter<Integer> parameter) {
        this.parameter = parameter;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:iomode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:iomode." + (parameter.getValue() == TileDiskManipulator.INSERT ? "insert" : "extract"));
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");

        gui.drawTexture(x, y + 1, parameter.getValue() == TileDiskManipulator.EXTRACT ? 0 : 16, 160, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() == TileDiskManipulator.INSERT ? TileDiskManipulator.EXTRACT : TileDiskManipulator.INSERT);
    }
}
