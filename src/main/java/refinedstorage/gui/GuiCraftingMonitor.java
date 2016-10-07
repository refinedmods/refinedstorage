package refinedstorage.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import refinedstorage.RS;
import refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import refinedstorage.container.ContainerCraftingMonitor;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.network.MessageCraftingMonitorCancel;
import refinedstorage.tile.TileCraftingMonitor;

import java.io.IOException;
import java.util.List;

public class GuiCraftingMonitor extends GuiBase {
    private static final int VISIBLE_ROWS = 5;

    private static final int ITEM_WIDTH = 143;
    private static final int ITEM_HEIGHT = 18;

    private TileCraftingMonitor craftingMonitor;

    private GuiButton cancelButton;
    private GuiButton cancelAllButton;

    private int itemSelected = -1;

    private int itemSelectedX = -1;
    private int itemSelectedY = -1;

    public GuiCraftingMonitor(ContainerCraftingMonitor container, TileCraftingMonitor craftingMonitor) {
        super(container, 176, 230);

        this.craftingMonitor = craftingMonitor;

        this.scrollbar = new Scrollbar(157, 20, 12, 89);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileCraftingMonitor.REDSTONE_MODE));

        String cancel = t("gui.cancel");
        String cancelAll = t("misc.refinedstorage:cancel_all");

        int cancelButtonWidth = 14 + fontRendererObj.getStringWidth(cancel);
        int cancelAllButtonWidth = 14 + fontRendererObj.getStringWidth(cancelAll);

        cancelButton = addButton(x + 7, y + 113, cancelButtonWidth, 20, cancel, false);
        cancelAllButton = addButton(x + 7 + cancelButtonWidth + 4, y + 113, cancelAllButtonWidth, 20, cancelAll, false);
    }

    @Override
    public void update(int x, int y) {
        scrollbar.setEnabled(getRows() > VISIBLE_ROWS);
        scrollbar.setMaxOffset(getRows() - VISIBLE_ROWS);

        if (itemSelected >= getElements().size()) {
            itemSelected = -1;
        }

        cancelButton.enabled = itemSelected != -1;
        cancelAllButton.enabled = getElements().size() > 0;
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_monitor.png");

        drawTexture(x, y, 0, 0, width, height);

        if (itemSelectedX != -1 && itemSelectedY != -1) {
            drawTexture(x + itemSelectedX, y + itemSelectedY, 0, 232, ITEM_WIDTH, ITEM_HEIGHT);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:crafting_monitor"));
        drawString(7, 137, t("container.inventory"));

        int item = scrollbar.getOffset();

        RenderHelper.enableGUIStandardItemLighting();

        int ox = 8;
        int x = ox;
        int y = 20;

        itemSelectedX = -1;
        itemSelectedY = -1;

        for (int i = 0; i < VISIBLE_ROWS; ++i) {
            if (item < getElements().size()) {
                ICraftingMonitorElement element = getElements().get(item);

                if (item == itemSelected) {
                    itemSelectedX = x;
                    itemSelectedY = y;
                }

                element.draw(this, x, y);

                x = ox;
                y += ITEM_HEIGHT;
            }

            item++;
        }
    }

    private int getRows() {
        return getElements().size();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == cancelButton && itemSelected != -1) {
            ICraftingMonitorElement element = getElements().get(itemSelected);

            if (element.getTaskId() != -1) {
                RS.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(craftingMonitor, element.getTaskId()));
            }
        } else if (button == cancelAllButton && getElements().size() > 0) {
            RS.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(craftingMonitor, -1));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        itemSelected = -1;

        if (mouseButton == 0 && inBounds(8, 20, 144, 90, mouseX - guiLeft, mouseY - guiTop)) {
            int item = scrollbar.getOffset();

            for (int i = 0; i < VISIBLE_ROWS; ++i) {
                int ix = 8;
                int iy = 20 + (i * ITEM_HEIGHT);

                if (inBounds(ix, iy, ITEM_WIDTH, ITEM_HEIGHT, mouseX - guiLeft, mouseY - guiTop) && (item + i) < getElements().size()) {
                    itemSelected = item + i;
                }
            }
        }
    }

    private List<ICraftingMonitorElement> getElements() {
        return TileCraftingMonitor.ELEMENTS.getValue();
    }
}
