package refinedstorage.gui;

import refinedstorage.api.util.IComparer;
import refinedstorage.container.ContainerDiskManipulator;
import refinedstorage.gui.sidebutton.*;
import refinedstorage.tile.TileDiskManipulator;

public class GuiDiskManipulator extends GuiBase {
    public GuiDiskManipulator(ContainerDiskManipulator container) {
        super(container, 211, 211);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileDiskManipulator.REDSTONE_MODE));
        addSideButton(new SideButtonIOMode(this, TileDiskManipulator.IO_MODE));
        addSideButton(new SideButtonType(this, TileDiskManipulator.TYPE));
        addSideButton(new SideButtonMode(this, TileDiskManipulator.MODE));
        addSideButton(new SideButtonCompare(this, TileDiskManipulator.COMPARE, IComparer.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(this, TileDiskManipulator.COMPARE, IComparer.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {

    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/disk_manipulator.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("block.refinedstorage:disk_manipulator.name"));
        drawString(7, 117, t("container.inventory"));
        drawString(25, 45, t("gui.refinedstorage:fluid_interface.in"));
        drawString(115, 45, t("gui.refinedstorage:fluid_interface.out"));
    }
}
