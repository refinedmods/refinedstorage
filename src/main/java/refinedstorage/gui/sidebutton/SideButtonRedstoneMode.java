package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonRedstoneMode extends SideButton {
    private TileDataParameter<Integer> parameter;

    public SideButtonRedstoneMode(TileDataParameter<Integer> parameter) {
        this.parameter = parameter;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.RED + gui.t("sidebutton.refinedstorage:redstone_mode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:redstone_mode." + parameter.getValue());
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");
        gui.drawTexture(x, y + 1, parameter.getValue() * 16, 0, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() + 1);
    }
}
