package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonVoidExcess extends SideButton {
    private TileDataParameter<Boolean> parameter;
    private String type;

    public SideButtonVoidExcess(GuiBase gui, TileDataParameter<Boolean> parameter, String type) {
        super(gui);

        this.parameter = parameter;
        this.type = type;
    }

    @Override
    public String getTooltip() {
        return TextFormatting.LIGHT_PURPLE + GuiBase.t("sidebutton.refinedstorage:void_excess." + type) + TextFormatting.RESET + "\n" + GuiBase.t(parameter.getValue() ? "gui.yes" : "gui.no");
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
