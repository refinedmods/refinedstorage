package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeDetector;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.tile.TileDetector;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideButtonDetectorMode extends SideButton {
    public SideButtonDetectorMode(BaseScreen gui) {
        super(gui);
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage:detector.mode") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage:detector.mode." + TileDetector.MODE.getValue());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        gui.blit(x, y, TileDetector.MODE.getValue() * 16, 176, 16, 16);
    }

    @Override
    public void onPress() {
        int mode = TileDetector.MODE.getValue();

        if (mode == NetworkNodeDetector.MODE_EQUAL) {
            mode = NetworkNodeDetector.MODE_ABOVE;
        } else if (mode == NetworkNodeDetector.MODE_ABOVE) {
            mode = NetworkNodeDetector.MODE_UNDER;
        } else if (mode == NetworkNodeDetector.MODE_UNDER) {
            mode = NetworkNodeDetector.MODE_EQUAL;
        }

        TileDataManager.setParameter(TileDetector.MODE, mode);
    }
}
