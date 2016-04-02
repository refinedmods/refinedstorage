package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.gui.GuiBase;
import refinedstorage.network.MessageRedstoneModeUpdate;
import refinedstorage.tile.config.IRedstoneModeConfig;

public class SideButtonRedstoneMode extends SideButton {
    private IRedstoneModeConfig config;

    public SideButtonRedstoneMode(IRedstoneModeConfig config) {
        this.config = config;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.RED + gui.t("sidebutton.refinedstorage:redstone_mode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:redstone_mode." + config.getRedstoneMode().id);
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");
        gui.drawTexture(x, y + 1, config.getRedstoneMode().id * 16, 0, 16, 16);
    }

    @Override
    public void actionPerformed() {
        RefinedStorage.NETWORK.sendToServer(new MessageRedstoneModeUpdate(config));
    }
}
