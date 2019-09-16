package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerNetworkTransmitter;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileNetworkTransmitter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiNetworkTransmitter extends GuiBase<ContainerNetworkTransmitter> {
    private TileNetworkTransmitter networkTransmitter;

    public GuiNetworkTransmitter(ContainerNetworkTransmitter container, TileNetworkTransmitter networkTransmitter, PlayerInventory inventory) {
        super(container, 176, 137, inventory, null);

        this.networkTransmitter = networkTransmitter;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileNetworkTransmitter.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/network_transmitter.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:network_transmitter"));

        String distance;

        if (networkTransmitter.getNode().getNetworkCard().getStackInSlot(0).isEmpty()) {
            distance = I18n.format("gui.refinedstorage:network_transmitter.missing_card");
        } else if (TileNetworkTransmitter.RECEIVER_DIMENSION.getValue() != networkTransmitter.getWorld().getDimension().getType().getId()) {
            distance = I18n.format("gui.refinedstorage:network_transmitter.dimension", TileNetworkTransmitter.RECEIVER_DIMENSION.getValue());
        } else if (TileNetworkTransmitter.DISTANCE.getValue() != -1) {
            distance = I18n.format("gui.refinedstorage:network_transmitter.distance", TileNetworkTransmitter.DISTANCE.getValue());
        } else {
            distance = I18n.format("gui.refinedstorage:network_transmitter.missing_card");
        }

        renderString(51, 24, distance);
        renderString(7, 42, I18n.format("container.inventory"));
    }
}