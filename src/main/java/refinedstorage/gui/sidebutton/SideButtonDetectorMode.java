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
        StringBuilder builder = new StringBuilder();

        builder.append(TextFormatting.GREEN).append(gui.t("sidebutton.refinedstorage:detector.mode")).append(TextFormatting.RESET).append("\n");
        builder.append(gui.t("sidebutton.refinedstorage:detector.mode." + detector.getMode()));

        return builder.toString();
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.drawItem(x, y, new ItemStack(Items.redstone, 1));
    }

    @Override
    public void actionPerformed() {
        RefinedStorage.NETWORK.sendToServer(new MessageDetectorModeUpdate(detector));
    }
}
