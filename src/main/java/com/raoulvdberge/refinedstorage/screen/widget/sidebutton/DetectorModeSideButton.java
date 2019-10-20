package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.DetectorNetworkNode;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.tile.DetectorTile;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class DetectorModeSideButton extends SideButton {
    public DetectorModeSideButton(BaseScreen screen) {
        super(screen);
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage.detector.mode") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.detector.mode." + DetectorTile.MODE.getValue());
    }

    @Override
    protected void renderButtonIcon(int x, int y) {
        screen.blit(x, y, DetectorTile.MODE.getValue() * 16, 176, 16, 16);
    }

    @Override
    public void onPress() {
        int mode = DetectorTile.MODE.getValue();

        if (mode == DetectorNetworkNode.MODE_EQUAL) {
            mode = DetectorNetworkNode.MODE_ABOVE;
        } else if (mode == DetectorNetworkNode.MODE_ABOVE) {
            mode = DetectorNetworkNode.MODE_UNDER;
        } else if (mode == DetectorNetworkNode.MODE_UNDER) {
            mode = DetectorNetworkNode.MODE_EQUAL;
        }

        TileDataManager.setParameter(DetectorTile.MODE, mode);
    }
}
