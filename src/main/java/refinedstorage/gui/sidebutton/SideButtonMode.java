package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public class SideButtonMode extends SideButton {
    private TileDataParameter<Integer> parameter;

    public SideButtonMode(TileDataParameter<Integer> parameter) {
        this.parameter = parameter;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:mode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:mode." + (parameter.getValue() == IModeConfig.WHITELIST ? "whitelist" : "blacklist"));
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");

        gui.drawTexture(x, y + 1, parameter.getValue() == IModeConfig.WHITELIST ? 0 : 16, 64, 16, 16);
    }

    @Override
    public void actionPerformed() {
        TileDataManager.setParameter(parameter, parameter.getValue() == IModeConfig.WHITELIST ? IModeConfig.BLACKLIST : IModeConfig.WHITELIST);
    }
}
