package refinedstorage.gui;

import refinedstorage.api.storage.CompareUtils;
import refinedstorage.container.ContainerImporter;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonMode;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileImporter;

public class GuiImporter extends GuiBase {
    private TileImporter importer;

    public GuiImporter(ContainerImporter container, TileImporter importer) {
        super(container, 211, 137);

        this.importer = importer;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(importer));

        addSideButton(new SideButtonMode(importer));

        addSideButton(new SideButtonCompare(importer, CompareUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(importer, CompareUtils.COMPARE_NBT));
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
