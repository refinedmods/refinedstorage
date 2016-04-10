package refinedstorage.gui;

import refinedstorage.container.ContainerCraftingMonitor;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.autocrafting.TileCraftingMonitor;

public class GuiCraftingMonitor extends GuiBase {
    public static final int VISIBLE_ROWS = 2;

    private TileCraftingMonitor craftingMonitor;

    private Scrollbar scrollbar = new Scrollbar(157, 20, 12, 59);

    public GuiCraftingMonitor(ContainerCraftingMonitor container, TileCraftingMonitor craftingMonitor) {
        super(container, 176, 181);

        this.craftingMonitor = craftingMonitor;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(craftingMonitor));
    }

    @Override
    public void update(int x, int y) {
        scrollbar.setCanScroll(getRows() > VISIBLE_ROWS);
        scrollbar.setScrollDelta((float) scrollbar.getScrollbarHeight() / (float) getRows());
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_monitor.png");

        drawTexture(x, y, 0, 0, width, height);

        scrollbar.draw(this);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        scrollbar.update(this, mouseX, mouseY);

        drawString(7, 7, t("gui.refinedstorage:crafting_monitor"));
        drawString(7, 87, t("container.inventory"));
    }

    private int getRows() {
        return 0;
    }
}
