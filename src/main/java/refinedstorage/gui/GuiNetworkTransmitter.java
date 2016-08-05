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
        addSideButton(new SideButtonRedstoneMode(TileNetworkTransmitter.REDSTONE_MODE));
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

        if (networkTransmitter.getNetworkCard().getStackInSlot(0) == null) {
            distance = t("gui.refinedstorage:network_transmitter.missing_card");
        } else if (!TileNetworkTransmitter.RECEIVER_DIMENSION_SUPPORTED.getValue()) {
            distance = t("gui.refinedstorage:network_transmitter.missing_upgrade");
        } else if (TileNetworkTransmitter.RECEIVER_DIMENSION.getValue() != networkTransmitter.getWorld().provider.getDimension()) {
            distance = t("gui.refinedstorage:network_transmitter.dimension", TileNetworkTransmitter.RECEIVER_DIMENSION.getValue());
        } else {
            distance = t("gui.refinedstorage:network_transmitter.distance", TileNetworkTransmitter.DISTANCE.getValue());
        }

        drawString(51, 24, distance);
        drawString(7, 42, t("container.inventory"));
    }
}