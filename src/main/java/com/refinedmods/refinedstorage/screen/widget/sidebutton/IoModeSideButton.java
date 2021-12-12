package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator.DiskManipulatorNetworkNode;
import com.refinedmods.refinedstorage.container.DiskManipulatorContainer;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class IoModeSideButton extends SideButton {
    public IoModeSideButton(BaseScreen<DiskManipulatorContainer> screen) {
        super(screen);
    }

    @Override
    public String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.iomode") + "\n" + TextFormatting.GRAY + I18n.get("sidebutton.refinedstorage.iomode." + (DiskManipulatorTile.IO_MODE.getValue() == DiskManipulatorNetworkNode.IO_MODE_INSERT ? "insert" : "extract"));
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        screen.blit(matrixStack, x, y, DiskManipulatorTile.IO_MODE.getValue() == DiskManipulatorNetworkNode.IO_MODE_EXTRACT ? 0 : 16, 160, 16, 16);
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(DiskManipulatorTile.IO_MODE, DiskManipulatorTile.IO_MODE.getValue() == DiskManipulatorNetworkNode.IO_MODE_INSERT ? DiskManipulatorNetworkNode.IO_MODE_EXTRACT : DiskManipulatorNetworkNode.IO_MODE_INSERT);
    }
}
