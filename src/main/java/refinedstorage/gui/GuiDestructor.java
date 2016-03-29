package refinedstorage.gui;

import refinedstorage.container.ContainerDestructor;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonMode;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileDestructor;
import refinedstorage.util.InventoryUtils;

public class GuiDestructor extends GuiBase {
    private TileDestructor destructor;

    public GuiDestructor(ContainerDestructor container, TileDestructor destructor) {
        super(container, 176, 137);

        this.destructor = destructor;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(destructor));

        addSideButton(new SideButtonMode(destructor));

        addSideButton(new SideButtonCompare(destructor, InventoryUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(destructor, InventoryUtils.COMPARE_NBT));
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
