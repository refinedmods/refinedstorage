package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.gui.GuiBase;
import refinedstorage.network.MessageRedstoneModeUpdate;
import refinedstorage.tile.settings.IRedstoneModeSetting;

public class SideButtonRedstoneMode extends SideButton {
    private IRedstoneModeSetting setting;

    public SideButtonRedstoneMode(IRedstoneModeSetting setting) {
        this.setting = setting;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        StringBuilder builder = new StringBuilder();

        builder.append(TextFormatting.RED).append(gui.t("sidebutton.refinedstorage:redstone_mode")).append(TextFormatting.RESET).append("\n");

        builder.append(gui.t("sidebutton.refinedstorage:redstone_mode." + setting.getRedstoneMode().id));

        return builder.toString();
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");
        gui.drawTexture(x, y + 1, setting.getRedstoneMode().id * 16, 0, 16, 16);
    }

    @Override
    public void actionPerformed() {
        RefinedStorage.NETWORK.sendToServer(new MessageRedstoneModeUpdate(setting));
    }
}
