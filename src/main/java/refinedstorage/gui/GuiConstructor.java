package refinedstorage.gui;

import refinedstorage.api.util.IComparer;
import refinedstorage.container.ContainerConstructor;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.gui.sidebutton.SideButtonType;
import refinedstorage.tile.TileConstructor;

public class GuiConstructor extends GuiBase {
    public GuiConstructor(ContainerConstructor container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileConstructor.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileConstructor.TYPE));

        addSideButton(new SideButtonCompare(this, TileConstructor.COMPARE, IComparer.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(this, TileConstructor.COMPARE, IComparer.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/constructor.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:constructor"));
        drawString(7, 43, t("container.inventory"));
    }
}
