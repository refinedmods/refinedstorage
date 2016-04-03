package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.gui.GuiBase;
import refinedstorage.network.MessageCompareUpdate;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.util.InventoryUtils;

public class SideButtonCompare extends SideButton {
    private ICompareConfig config;
    private int mask;

    public SideButtonCompare(ICompareConfig config, int mask) {
        this.config = config;
        this.mask = mask;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        String tooltip = TextFormatting.YELLOW + gui.t("sidebutton.refinedstorage:compare." + mask) + TextFormatting.RESET + "\n";

        if ((config.getCompare() & mask) == mask) {
            tooltip += gui.t("misc.refinedstorage:yes");
        } else {
            tooltip += gui.t("misc.refinedstorage:no");
        }

        return tooltip;
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

        int tx = (config.getCompare() & mask) == mask ? 0 : 16;

        gui.drawTexture(x, y + 1, tx, ty, 16, 16);
    }

    @Override
    public void actionPerformed() {
        RefinedStorage.NETWORK.sendToServer(new MessageCompareUpdate(config, config.getCompare() ^ mask));
    }
}
