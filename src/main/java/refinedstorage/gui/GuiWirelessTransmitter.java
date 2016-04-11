package refinedstorage.gui;

import refinedstorage.container.ContainerWirelessTransmitter;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileWirelessTransmitter;

public class GuiWirelessTransmitter extends GuiBase {
    private TileWirelessTransmitter wirelessTransmitter;

    public GuiWirelessTransmitter(ContainerWirelessTransmitter container, TileWirelessTransmitter wirelessTransmitter) {
        super(container, 176, 137);

        this.wirelessTransmitter = wirelessTransmitter;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(wirelessTransmitter));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/wireless_transmitter.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:wireless_transmitter", wirelessTransmitter.getRange()));
        drawString(7, 43, t("container.inventory"));
    }
}
