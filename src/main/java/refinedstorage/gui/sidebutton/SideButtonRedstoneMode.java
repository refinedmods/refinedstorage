package refinedstorage.gui.sidebutton;

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
    public String getTooltip() {
        return TextFormatting.RED + GuiBase.t("sidebutton.refinedstorage:redstone_mode") + TextFormatting.RESET + "\n" + GuiBase.t("sidebutton.refinedstorage:redstone_mode." + parameter.getValue());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, parameter.getValue() * 16, 0, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() + 1);
    }
}
