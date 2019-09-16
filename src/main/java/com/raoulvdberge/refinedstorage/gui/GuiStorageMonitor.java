package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerStorageMonitor;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonCompare;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiStorageMonitor extends GuiBase<ContainerStorageMonitor> {
    public GuiStorageMonitor(ContainerStorageMonitor container, PlayerInventory inventory) {
        super(container, 211, 137, inventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonCompare(this, TileStorageMonitor.COMPARE, IComparer.COMPARE_NBT));
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
