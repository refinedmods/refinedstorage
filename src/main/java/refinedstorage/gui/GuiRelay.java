package refinedstorage.gui;

import refinedstorage.container.ContainerRelay;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileRelay;

public class GuiRelay extends GuiBase {
    public GuiRelay(ContainerRelay container) {
        super(container, 176, 131);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileRelay.REDSTONE_MODE));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/relay.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:relay"));
        drawString(7, 39, t("container.inventory"));
    }
}
