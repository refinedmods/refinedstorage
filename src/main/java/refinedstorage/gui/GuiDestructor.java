package refinedstorage.gui;

import refinedstorage.api.storage.CompareUtils;
import refinedstorage.container.ContainerDestructor;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonMode;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileDestructor;

public class GuiDestructor extends GuiBase {
    private TileDestructor destructor;

    public GuiDestructor(ContainerDestructor container, TileDestructor destructor) {
        super(container, 211, 137);

        this.destructor = destructor;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(destructor));

        addSideButton(new SideButtonMode(destructor));

        addSideButton(new SideButtonCompare(destructor, CompareUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(destructor, CompareUtils.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/destructor.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:destructor"));
        drawString(7, 43, t("container.inventory"));
    }
}
