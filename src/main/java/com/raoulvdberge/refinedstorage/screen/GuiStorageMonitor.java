package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.StorageMonitorContainer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiStorageMonitor extends BaseScreen<StorageMonitorContainer> {
    public GuiStorageMonitor(StorageMonitorContainer container, PlayerInventory inventory) {
        super(container, 211, 137, inventory, null);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new ExactModeSideButton(this, TileStorageMonitor.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/storage_monitor.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:storage_monitor"));
        renderString(7, 43, I18n.format("container.inventory"));
    }
}
