package refinedstorage.gui;

import refinedstorage.container.ContainerCrafter;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileCrafter;

public class GuiCrafter extends GuiBase {
    private TileCrafter crafter;

    public GuiCrafter(ContainerCrafter container, TileCrafter crafter) {
        super(container, 211, 137);

        this.crafter = crafter;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(crafter));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafter.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:crafter"));
        drawString(7, 43, t("container.inventory"));
    }
}
