package refinedstorage.gui;

import refinedstorage.container.ContainerConstructor;
import refinedstorage.gui.sidebutton.SideButtonCompare;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileConstructor;
import refinedstorage.util.InventoryUtils;

public class GuiConstructor extends GuiBase {
    private TileConstructor constructor;

    public GuiConstructor(ContainerConstructor container, TileConstructor constructor) {
        super(container, 211, 137);

        this.constructor = constructor;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(constructor));

        addSideButton(new SideButtonCompare(constructor, InventoryUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(constructor, InventoryUtils.COMPARE_NBT));
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
