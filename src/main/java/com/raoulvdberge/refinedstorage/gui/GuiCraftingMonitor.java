package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawer;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.gui.control.Scrollbar;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonGridSize;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.network.MessageCraftingMonitorCancel;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GuiCraftingMonitor extends GuiBase implements IResizableDisplay {
    public class CraftingMonitorElementDrawers extends ElementDrawers {
        private IElementDrawer<Integer> overlayDrawer = (x, y, color) -> {
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableLighting();
            drawRect(x, y, x + ITEM_WIDTH, y + ITEM_HEIGHT - 1, color);
        };

        @Override
        public IElementDrawer<Integer> getOverlayDrawer() {
            return overlayDrawer;
        }
    }

    private static final int VISIBLE_ROWS = 5;

    private static final int ITEM_WIDTH = 143;
    private static final int ITEM_HEIGHT = 18;

    private GuiButton cancelButton;
    private GuiButton cancelAllButton;

    private ICraftingMonitor craftingMonitor;
    private List<ICraftingMonitorElement> elements = Collections.emptyList();

    private IElementDrawers drawers = new CraftingMonitorElementDrawers();

    private int itemSelected = -1;
    private int itemSelectedX = -1;
    private int itemSelectedY = -1;

    public GuiCraftingMonitor(ContainerCraftingMonitor container, ICraftingMonitor craftingMonitor) {
        super(container, 176, 230);

        this.craftingMonitor = craftingMonitor;
    }

    public void setElements(List<ICraftingMonitorElement> elements) {
        this.elements = elements;
    }

    private List<ICraftingMonitorElement> getElements() {
        return craftingMonitor.isActive() ? elements : Collections.emptyList();
    }

    @Override
    public void init(int x, int y) {
        ((ContainerCraftingMonitor) this.inventorySlots).initSlots();

        this.scrollbar = new Scrollbar(157, getTopHeight(), 12, (getVisibleRows() * 18) - 1);

        if (craftingMonitor.getRedstoneModeParameter() != null) {
            addSideButton(new SideButtonRedstoneMode(this, craftingMonitor.getRedstoneModeParameter()));
        }

        addSideButton(new SideButtonGridSize(this, () -> craftingMonitor.getSize(), size -> craftingMonitor.onSizeChanged(size)));

        String cancel = t("gui.cancel");
        String cancelAll = t("misc.refinedstorage:cancel_all");

        int cancelButtonWidth = 14 + fontRenderer.getStringWidth(cancel);
        int cancelAllButtonWidth = 14 + fontRenderer.getStringWidth(cancelAll);

        int by = y + getTopHeight() + (getVisibleRows() * 18) + 3;

        this.cancelButton = addButton(x + 7, by, cancelButtonWidth, 20, cancel, false, true);
        this.cancelAllButton = addButton(x + 7 + cancelButtonWidth + 4, by, cancelAllButtonWidth, 20, cancelAll, false, true);
    }

    private void updateScrollbar() {
        if (scrollbar != null) {
            scrollbar.setEnabled(getRows() > getVisibleRows());
            scrollbar.setMaxOffset(getRows() - getVisibleRows());
        }
    }

    @Override
    public void update(int x, int y) {
        updateScrollbar();

        if (itemSelected >= getElements().size()) {
            itemSelected = -1;
        }

        if (cancelButton != null) {
            cancelButton.enabled = itemSelected != -1 && getElements().get(itemSelected).getTaskId() != -1;
        }

        if (cancelAllButton != null) {
            cancelAllButton.enabled = getElements().size() > 0;
        }
    }

    @Override
    protected void calcHeight() {
        this.ySize = getTopHeight() + getBottomHeight() + (getVisibleRows() * 18);
        this.screenHeight = ySize;
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_monitor.png");

        int yy = y;

        drawTexture(x, yy, 0, 0, screenWidth, getTopHeight());

        yy += getTopHeight();

        int rows = getVisibleRows();

        for (int i = 0; i < rows; ++i) {
            drawTexture(x, yy, 0, getTopHeight() + (i > 0 ? (i == rows - 1 ? 18 * 2 : 18) : 0), screenWidth, 18);

            yy += 18;
        }

        drawTexture(x, yy, 0, getTopHeight() + (18 * 3), screenWidth, getBottomHeight());
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t(craftingMonitor.getGuiTitle()));
        drawString(7, getYPlayerInventory() - 12, t("container.inventory"));

        int item = scrollbar != null ? scrollbar.getOffset() : 0;

        RenderHelper.enableGUIStandardItemLighting();

        int x = 8;
        int y = 20;

        this.itemSelectedX = -1;
        this.itemSelectedY = -1;
        String itemSelectedTooltip = null;

        for (int i = scrollbar.getOffset(); i < scrollbar.getOffset() + getVisibleRows(); ++i) {
            if (item < getElements().size()) {
                ICraftingMonitorElement element = getElements().get(item);

                if (item == itemSelected) {
                    this.itemSelectedX = x;
                    this.itemSelectedY = y;
                }

                if (inBounds(x, y, ITEM_WIDTH, ITEM_HEIGHT, mouseX, mouseY)) {
                    itemSelectedTooltip = element.getTooltip();
                }

                element.draw(x, y, drawers, item == itemSelected);

                y += ITEM_HEIGHT;
            }

            item++;
        }

        if (itemSelectedTooltip != null && !itemSelectedTooltip.isEmpty()) {
            drawTooltip(mouseX, mouseY, I18n.format(itemSelectedTooltip));
        }
    }

    @Override
    public int getVisibleRows() {
        switch (craftingMonitor.getSize()) {
            case IGrid.SIZE_STRETCH:
                int screenSpaceAvailable = height - getTopHeight() - getBottomHeight();

                return Math.max(3, Math.min((screenSpaceAvailable / 18) - 3, RS.INSTANCE.config.maxRowsStretch));
            case IGrid.SIZE_SMALL:
                return 3;
            case IGrid.SIZE_MEDIUM:
                return 5;
            case IGrid.SIZE_LARGE:
                return 8;
            default:
                return 3;
        }
    }

    @Override
    public int getRows() {
        return getElements().size();
    }

    @Override
    public int getCurrentOffset() {
        return scrollbar.getOffset();
    }

    @Override
    public String getSearchFieldText() {
        return null;
    }

    @Override
    public int getTopHeight() {
        return 20;
    }

    @Override
    public int getBottomHeight() {
        return 120;
    }

    @Override
    public int getYPlayerInventory() {
        return getTopHeight() + (18 * getVisibleRows()) + 38;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == cancelButton && itemSelected != -1) {
            ICraftingMonitorElement element = getElements().get(itemSelected);

            if (element.getTaskId() != -1) {
                RS.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(element.getTaskId()));
            }
        } else if (button == cancelAllButton && getElements().size() > 0) {
            RS.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(-1));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.itemSelected = -1;

        if (mouseButton == 0) {
            int item = scrollbar != null ? scrollbar.getOffset() : 0;

            for (int i = 0; i < getVisibleRows(); ++i) {
                int ix = 8;
                int iy = 20 + (i * ITEM_HEIGHT);

                if (inBounds(ix, iy, ITEM_WIDTH, ITEM_HEIGHT, mouseX - guiLeft, mouseY - guiTop) && (item + i) < getElements().size()) {
                    this.itemSelected = item + i;
                }
            }
        }
    }
}
