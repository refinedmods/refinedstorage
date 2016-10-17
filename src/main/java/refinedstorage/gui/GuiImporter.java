package refinedstorage.gui;

import refinedstorage.api.util.IComparer;
import refinedstorage.container.ContainerImporter;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonMode;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.gui.sidebutton.SideButtonType;
import refinedstorage.tile.TileImporter;

public class GuiImporter extends GuiBase {
    public GuiImporter(ContainerImporter container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileImporter.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileImporter.TYPE));

        addSideButton(new SideButtonMode(this, TileImporter.MODE));

        addSideButton(new SideButtonCompare(this, TileImporter.COMPARE, IComparer.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(this, TileImporter.COMPARE, IComparer.COMPARE_NBT));
        addSideButton(new SideButtonCompare(this, TileImporter.COMPARE, IComparer.COMPARE_OREDICT));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/importer.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:importer"));
        drawString(7, 43, t("container.inventory"));
    }
}
