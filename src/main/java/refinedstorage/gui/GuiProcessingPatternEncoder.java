package refinedstorage.gui;

import refinedstorage.container.ContainerProcessingPatternEncoder;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonMode;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.autocrafting.TileProcessingPatternEncoder;
import refinedstorage.util.InventoryUtils;

public class GuiProcessingPatternEncoder extends GuiBase {
    public GuiProcessingPatternEncoder(ContainerProcessingPatternEncoder container, TileProcessingPatternEncoder ppEncoder) {
        super(container, 176, 172);
    }

    @Override
    public void init(int x, int y) {
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/processing_pattern_encoder.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:processing_pattern_encoder"));
        drawString(7, 77, t("container.inventory"));
    }
}
