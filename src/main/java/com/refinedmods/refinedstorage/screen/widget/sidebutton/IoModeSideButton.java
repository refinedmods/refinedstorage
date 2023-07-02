package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator.DiskManipulatorNetworkNode;
import com.refinedmods.refinedstorage.blockentity.DiskManipulatorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.container.DiskManipulatorContainerMenu;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class IoModeSideButton extends SideButton {
    public IoModeSideButton(BaseScreen<DiskManipulatorContainerMenu> screen) {
        super(screen);
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.refinedstorage.iomode") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.iomode." + (DiskManipulatorBlockEntity.IO_MODE.getValue() == DiskManipulatorNetworkNode.IO_MODE_INSERT ? "insert" : "extract"));
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, DiskManipulatorBlockEntity.IO_MODE.getValue() == DiskManipulatorNetworkNode.IO_MODE_EXTRACT ? 0 : 16, 160, 16, 16);
    }

    @Override
    public void onPress() {
        BlockEntitySynchronizationManager.setParameter(DiskManipulatorBlockEntity.IO_MODE, DiskManipulatorBlockEntity.IO_MODE.getValue() == DiskManipulatorNetworkNode.IO_MODE_INSERT ? DiskManipulatorNetworkNode.IO_MODE_EXTRACT : DiskManipulatorNetworkNode.IO_MODE_INSERT);
    }
}
