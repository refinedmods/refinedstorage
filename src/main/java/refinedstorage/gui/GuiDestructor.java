package refinedstorage.gui;

import refinedstorage.api.util.IComparer;
import refinedstorage.container.ContainerDestructor;
import refinedstorage.gui.sidebutton.*;
import refinedstorage.tile.TileDestructor;

public class GuiDestructor extends GuiBase {
    public GuiDestructor(ContainerDestructor container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileDestructor.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileDestructor.TYPE));

        addSideButton(new SideButtonMode(this, TileDestructor.MODE));

        addSideButton(new SideButtonCompare(this, TileDestructor.COMPARE, IComparer.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(this, TileDestructor.COMPARE, IComparer.COMPARE_NBT));
        addSideButton(new SideButtonCompare(this, TileDestructor.COMPARE, IComparer.COMPARE_OREDICT));

        addSideButton(new SideButtonPickup(this));
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
