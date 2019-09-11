package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.container.ContainerRelay;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileRelay;
import net.minecraft.entity.player.PlayerInventory;

public class GuiRelay extends GuiBase<ContainerRelay> {
    public GuiRelay(ContainerRelay container, PlayerInventory inventory) {
        super(container, 176, 131, inventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileRelay.REDSTONE_MODE));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/relay.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:relay"));
        drawString(7, 39, t("container.inventory"));
    }
}
