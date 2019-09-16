package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerWirelessTransmitter;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileWirelessTransmitter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiWirelessTransmitter extends GuiBase<ContainerWirelessTransmitter> {
    public GuiWirelessTransmitter(ContainerWirelessTransmitter container, PlayerInventory inventory) {
        super(container, 211, 137, inventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileWirelessTransmitter.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/wireless_transmitter.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:wireless_transmitter"));
        renderString(28, 25, I18n.format("gui.refinedstorage:wireless_transmitter.distance", TileWirelessTransmitter.RANGE.getValue()));
        renderString(7, 43, I18n.format("container.inventory"));
    }
}
