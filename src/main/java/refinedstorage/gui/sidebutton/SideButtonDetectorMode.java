package refinedstorage.gui.sidebutton;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.TileDetector;

public class SideButtonDetectorMode extends SideButton {
    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:detector.mode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:detector.mode." + TileDetector.MODE.getValue());
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.drawItem(x, y, new ItemStack(Items.REDSTONE, 1));
    }

    @Override
    public void actionPerformed() {
        int mode = TileDetector.MODE.getValue();

        if (mode == TileDetector.MODE_EQUAL) {
            mode = TileDetector.MODE_ABOVE;
        } else if (mode == TileDetector.MODE_ABOVE) {
            mode = TileDetector.MODE_UNDER;
        } else if (mode == TileDetector.MODE_UNDER) {
            mode = TileDetector.MODE_EQUAL;
        }

        TileDetector.MODE.setValue(mode);
    }
}
