package refinedstorage.gui;

import refinedstorage.api.storage.CompareUtils;
import refinedstorage.container.ContainerFluidDestructor;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonMode;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileFluidDestructor;

public class GuiFluidDestructor extends GuiBase {
    public GuiFluidDestructor(ContainerFluidDestructor container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileFluidDestructor.REDSTONE_MODE));

        addSideButton(new SideButtonMode(TileFluidDestructor.MODE));

        addSideButton(new SideButtonCompare(TileFluidDestructor.COMPARE, CompareUtils.COMPARE_NBT));
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
        drawString(7, 7, t("gui.refinedstorage:fluid_destructor"));
        drawString(7, 43, t("container.inventory"));
    }
}
