package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonVoidExcess extends SideButton {
    private TileDataParameter<Boolean> parameter;

    public SideButtonVoidExcess(GuiBase gui, TileDataParameter<Boolean> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    public String getTooltip() {
        return TextFormatting.LIGHT_PURPLE + gui.t("sidebutton.refinedstorage:void_excess.mode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:void_excess.mode." + (parameter.getValue() ? "on" : "off"));
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, parameter.getValue() ? 16 : 0, 192, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, !parameter.getValue());
    }
}
