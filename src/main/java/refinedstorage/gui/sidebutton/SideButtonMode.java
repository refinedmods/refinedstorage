package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.gui.GuiBase;
import refinedstorage.network.MessageModeToggle;
import refinedstorage.tile.config.IModeConfig;

public class SideButtonMode extends SideButton {
    private IModeConfig config;

    public SideButtonMode(IModeConfig config) {
        this.config = config;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        StringBuilder builder = new StringBuilder();

        builder.append(TextFormatting.GREEN).append(gui.t("sidebutton.refinedstorage:mode")).append(TextFormatting.RESET).append("\n");
        builder.append(gui.t("sidebutton.refinedstorage:mode." + (config.isWhitelist() ? "whitelist" : "blacklist")));

        return builder.toString();
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");

        int tx = 0;

        if (config.isWhitelist()) {
            tx = 0;
        } else if (config.isBlacklist()) {
            tx = 16;
        }

        gui.drawTexture(x, y + 1, tx, 64, 16, 16);
    }

    @Override
    public void actionPerformed() {
        RefinedStorage.NETWORK.sendToServer(new MessageModeToggle(config));
    }
}
