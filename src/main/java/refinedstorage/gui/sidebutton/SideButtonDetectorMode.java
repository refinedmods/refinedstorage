package refinedstorage.gui.sidebutton;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import refinedstorage.RefinedStorage;
import refinedstorage.gui.GuiBase;
import refinedstorage.network.MessageDetectorModeUpdate;
import refinedstorage.tile.TileDetector;

public class SideButtonDetectorMode extends SideButton {
    private TileDetector detector;

    public SideButtonDetectorMode(TileDetector detector) {
        this.detector = detector;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.GREEN + gui.t("sidebutton.refinedstorage:detector.mode") + TextFormatting.RESET + "\n" + gui.t("sidebutton.refinedstorage:detector.mode." + detector.getMode());
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.drawItem(x, y, new ItemStack(Items.REDSTONE, 1));
    }

    @Override
    public void actionPerformed() {
        RefinedStorage.INSTANCE.network.sendToServer(new MessageDetectorModeUpdate(detector));
    }
}
