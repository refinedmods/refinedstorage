package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.config.IFilterable;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonMode extends SideButton {
    private TileDataParameter<Integer> parameter;

    public SideButtonMode(GuiBase gui, TileDataParameter<Integer> parameter) {
        super(gui);

        this.parameter = parameter;
    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:mode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:mode." + (parameter.getValue() == IFilterable.WHITELIST ? "whitelist" : "blacklist"));
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, parameter.getValue() == IFilterable.WHITELIST ? 0 : 16, 64, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() == IFilterable.WHITELIST ? IFilterable.BLACKLIST : IFilterable.WHITELIST);
    }
}
