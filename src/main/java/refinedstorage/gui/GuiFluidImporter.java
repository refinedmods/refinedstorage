package refinedstorage.gui;

import refinedstorage.api.storage.CompareUtils;
import refinedstorage.container.ContainerFluidImporter;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonMode;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileFluidImporter;

public class GuiFluidImporter extends GuiBase {
    public GuiFluidImporter(ContainerFluidImporter container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileFluidImporter.REDSTONE_MODE));

        addSideButton(new SideButtonMode(TileFluidImporter.MODE));

        addSideButton(new SideButtonCompare(TileFluidImporter.COMPARE, CompareUtils.COMPARE_NBT));
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
        drawString(7, 7, t("gui.refinedstorage:fluid_importer"));
        drawString(7, 43, t("container.inventory"));
    }
}
