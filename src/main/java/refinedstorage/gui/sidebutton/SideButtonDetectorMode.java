package refinedstorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import refinedstorage.gui.GuiBase;
import refinedstorage.tile.TileDetector;
import refinedstorage.tile.data.TileDataManager;

public class SideButtonDetectorMode extends SideButton {
    public SideButtonDetectorMode(GuiBase gui) {
        super(gui);
    }

    @Override
    public String getTooltip() {
        return TextFormatting.GREEN + GuiBase.t("sidebutton.refinedstorage:detector.mode") + TextFormatting.RESET + "\n" + GuiBase.t("sidebutton.refinedstorage:detector.mode." + TileDetector.MODE.getValue());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.drawTexture(x, y, TileDetector.MODE.getValue() * 16, 176, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int mode = TileDetector.MODE.getValue();

        if (mode == TileDetector.MODE_EQUAL) {
            mode = TileDetector.MODE_ABOVE;
        } else if (mode == TileDetector.MODE_ABOVE) {
            mode = TileDetector.MODE_UNDER;
        } else if (mode == TileDetector.MODE_UNDER) {
            mode = TileDetector.MODE_AUTOCRAFTING;
        } else if (mode == TileDetector.MODE_AUTOCRAFTING) {
            mode = TileDetector.MODE_EQUAL;
        }

        TileDataManager.setParameter(TileDetector.MODE, mode);
    }
}
