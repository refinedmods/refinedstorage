package refinedstorage.gui;

import refinedstorage.container.ContainerGridFilter;

public class GuiGridFilter extends GuiBase {
    public GuiGridFilter(ContainerGridFilter container) {
        super(container, 176, 148);
    }

    @Override
    public void init(int x, int y) {
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/grid_filter.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:grid_filter"));
        drawString(7, 55, t("container.inventory"));
    }
}
