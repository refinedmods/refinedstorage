package refinedstorage.gui;

import refinedstorage.container.ContainerNetworkTransmitter;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileNetworkTransmitter;

public class GuiNetworkTransmitter extends GuiBase {
    private TileNetworkTransmitter networkTransmitter;

    public GuiNetworkTransmitter(ContainerNetworkTransmitter container, TileNetworkTransmitter networkTransmitter) {
        super(container, 210, 137);

        this.networkTransmitter = networkTransmitter;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(networkTransmitter));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/network_transmitter.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:network_transmitter"));

        String distance;

        if (!networkTransmitter.isInSameDimension()) {
            distance = t("gui.refinedstorage:network_transmitter.different_dimension");
        } else if (networkTransmitter.getDistance() == -1) {
            distance = t("gui.refinedstorage:network_transmitter.missing_card");
        } else {
            distance = t("gui.refinedstorage:network_transmitter.distance", networkTransmitter.getDistance());
        }

        drawString(51, 24, distance);
        drawString(7, 42, t("container.inventory"));
    }
}