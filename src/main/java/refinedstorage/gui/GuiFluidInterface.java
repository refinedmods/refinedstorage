package refinedstorage.gui;

import refinedstorage.container.ContainerFluidInterface;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileFluidInterface;

public class GuiFluidInterface extends GuiBase {
    public GuiFluidInterface(ContainerFluidInterface container) {
        super(container, 211, 204);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileFluidInterface.REDSTONE_MODE));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/fluid_interface.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:fluid_interface"));
        drawString(43 + 4, 20, t("gui.refinedstorage:fluid_interface.in"));
        drawString(115 + 1, 20, t("gui.refinedstorage:fluid_interface.out"));
        drawString(7, 111, t("container.inventory"));
    }
}
