package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.gui.GuiBase;
import refinedstorage.network.MessageCompareUpdate;
import refinedstorage.tile.settings.ICompareSetting;
import refinedstorage.util.InventoryUtils;

public class SideButtonCompare extends SideButton {
    private ICompareSetting setting;
    private int mask;

    public SideButtonCompare(ICompareSetting setting, int mask) {
        this.setting = setting;
        this.mask = mask;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        StringBuilder builder = new StringBuilder();

        builder.append(TextFormatting.YELLOW).append(gui.t("sidebutton.refinedstorage:compare." + mask)).append(TextFormatting.RESET).append("\n");

        if ((setting.getCompare() & mask) == mask) {
            builder.append(gui.t("misc.refinedstorage:yes"));
        } else {
            builder.append(gui.t("misc.refinedstorage:no"));
        }

        return builder.toString();
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");

        int ty = 0;

        if (mask == InventoryUtils.COMPARE_DAMAGE) {
            ty = 80;
        } else if (mask == InventoryUtils.COMPARE_NBT) {
            ty = 48;
        }

        int tx = (setting.getCompare() & mask) == mask ? 0 : 16;

        gui.drawTexture(x, y + 2, tx, ty, 16, 16);
    }

    @Override
    public void actionPerformed() {
        RefinedStorage.NETWORK.sendToServer(new MessageCompareUpdate(setting, setting.getCompare() ^ mask));
    }
}
