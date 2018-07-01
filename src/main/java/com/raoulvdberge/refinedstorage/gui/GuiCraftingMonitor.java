package com.raoulvdberge.refinedstorage.gui;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawer;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.gui.control.Scrollbar;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonGridSize;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.gui.control.TabList;
import com.raoulvdberge.refinedstorage.network.MessageCraftingMonitorCancel;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    public static class CraftingMonitorTask implements IGridTab {
        private UUID id;
        private ItemStack requested;
        private int qty;
        private long executionStarted;
        private List<ICraftingMonitorElement> elements;

        public CraftingMonitorTask(UUID id, ItemStack requested, int qty, long executionStarted, List<ICraftingMonitorElement> elements) {
            this.id = id;
            this.requested = requested;
            this.qty = qty;
            this.executionStarted = executionStarted;
            this.elements = elements;
        }

        @Override
        public List<IFilter> getFilters() {
            return null;
        }

        @Override
        public void drawTooltip(int x, int y, int screenWidth, int screenHeight, FontRenderer fontRenderer) {
            List<String> textLines = Lists.newArrayList(requested.getDisplayName());
            List<String> smallTextLines = Lists.newArrayList();

            int totalSecs = (int) (System.currentTimeMillis() - executionStarted) / 1000;
            int minutes = (totalSecs % 3600) / 60;
            int seconds = totalSecs % 60;

            smallTextLines.add(I18n.format("gui.refinedstorage:crafting_monitor.tooltip.requested", qty));
            smallTextLines.add(String.format("%02d:%02d", minutes, seconds));

            RenderUtils.drawTooltipWithSmallText(textLines, smallTextLines, true, ItemStack.EMPTY, x, y, screenWidth, screenHeight, fontRenderer);
        }

        @Override
        public ItemStack getIcon() {
            return requested;
        }
    }

    private static final int ITEM_WIDTH = 143;
    private static final int ITEM_HEIGHT = 18;

    private GuiButton cancelButton;
    private GuiButton cancelAllButton;

    private ICraftingMonitor craftingMonitor;

    private List<IGridTab> tasks = Collections.emptyList();
    private TabList tabs;

    private int elementSelected = -1;

    private IElementDrawers drawers = new CraftingMonitorElementDrawers();

    public GuiCraftingMonitor(ContainerCraftingMonitor container, ICraftingMonitor craftingMonitor) {
        super(container, 176, 230);

        this.craftingMonitor = craftingMonitor;

        this.tabs = new TabList(this, () -> tasks, () -> (int) Math.floor((float) Math.max(0, tasks.size() - 1) / (float) ICraftingMonitor.TABS_PER_PAGE), craftingMonitor::getTabPage, () -> {
            IGridTab tab = getCurrentTab();

            if (tab == null) {
                return -1;
            }

            return tasks.indexOf(tab);
        }, ICraftingMonitor.TABS_PER_PAGE);

        this.tabs.addListener(new TabList.ITabListListener() {
            @Override
            public void onSelectionChanged(int tab) {
                craftingMonitor.onTabSelectionChanged(Optional.of(((CraftingMonitorTask) tasks.get(tab)).id));

                scrollbar.setOffset(0);
            }

            @Override
            public void onPageChanged(int page) {
                craftingMonitor.onTabPageChanged(page);
            }
        });
    }

    public void setTasks(List<IGridTab> tasks) {
        this.tasks = tasks;
    }

    public List<ICraftingMonitorElement> getElements() {
        IGridTab tab = getCurrentTab();

        if (tab == null) {
            return Collections.emptyList();
        }

        return ((CraftingMonitorTask) tab).elements;
    }

    @Override
    public void init(int x, int y) {
        ((ContainerCraftingMonitor) this.inventorySlots).initSlots();

        this.tabs.init(xSize);

        this.scrollbar = new Scrollbar(157, getTopHeight() + tabs.getHeight(), 12, (getVisibleRows() * 18) - 1);

        if (craftingMonitor.getRedstoneModeParameter() != null) {
            addSideButton(new SideButtonRedstoneMode(this, craftingMonitor.getRedstoneModeParameter()));
        }

        addSideButton(new SideButtonGridSize(this, () -> craftingMonitor.getSize(), size -> craftingMonitor.onSizeChanged(size)));

        String cancel = t("gui.cancel");
        String cancelAll = t("misc.refinedstorage:cancel_all");

        int cancelButtonWidth = 14 + fontRenderer.getStringWidth(cancel);
        int cancelAllButtonWidth = 14 + fontRenderer.getStringWidth(cancelAll);

        int by = y + getTopHeight() + (getVisibleRows() * 18) + 3 + tabs.getHeight();

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

        this.tabs.update();

        if (elementSelected >= getElements().size()) {
            elementSelected = -1;
        }

        if (cancelButton != null) {
            cancelButton.enabled = hasValidTabSelected();
        }

        if (cancelAllButton != null) {
            cancelAllButton.enabled = tasks.size() > 0;
        }
    }

    private boolean hasValidTabSelected() {
        return getCurrentTab() != null;
    }

    @Nullable
    private IGridTab getCurrentTab() {
        Optional<UUID> currentTab = craftingMonitor.getTabSelected();

        if (currentTab.isPresent()) {
            return getTabById(currentTab.get());
        }

        return null;
    }

    @Nullable
    private IGridTab getTabById(UUID id) {
        return tasks.stream().filter(t -> ((CraftingMonitorTask) t).id.equals(id)).findFirst().orElse(null);
    }

    @Override
    protected void calcHeight() {
        this.ySize = getTopHeight() + getBottomHeight() + (getVisibleRows() * 18) + tabs.getHeight();
        this.screenHeight = ySize;
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        tabs.drawBackground(x, y);

        bindTexture("gui/crafting_monitor.png");

        int yy = y + tabs.getHeight();

        drawTexture(x, yy, 0, 0, screenWidth, getTopHeight());

        yy += getTopHeight();

        int rows = getVisibleRows();

        for (int i = 0; i < rows; ++i) {
            drawTexture(x, yy, 0, getTopHeight() + (i > 0 ? (i == rows - 1 ? 18 * 2 : 18) : 0), screenWidth, 18);

            yy += 18;
        }

        drawTexture(x, yy, 0, getTopHeight() + (18 * 3), screenWidth, getBottomHeight());

        tabs.drawForeground(x, y, mouseX, mouseY);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7 + tabs.getHeight(), t(craftingMonitor.getGuiTitle()));
        drawString(7, getYPlayerInventory() - 12, t("container.inventory"));

        int item = scrollbar != null ? scrollbar.getOffset() : 0;

        RenderHelper.enableGUIStandardItemLighting();

        int x = 8;
        int y = 20 + tabs.getHeight();

        String itemSelectedTooltip = null;

        for (int i = scrollbar.getOffset(); i < scrollbar.getOffset() + getVisibleRows(); ++i) {
            if (item < getElements().size()) {
                ICraftingMonitorElement element = getElements().get(item);

                if (inBounds(x, y, ITEM_WIDTH, ITEM_HEIGHT, mouseX, mouseY)) {
                    itemSelectedTooltip = element.getTooltip();
                }

                element.draw(x, y, drawers, item == elementSelected);

                y += ITEM_HEIGHT;
            }

            item++;
        }

        if (itemSelectedTooltip != null && !itemSelectedTooltip.isEmpty()) {
            drawTooltip(mouseX, mouseY, I18n.format(itemSelectedTooltip));
        }

        tabs.drawTooltip(fontRenderer, mouseX, mouseY);
    }

    @Override
    public int getVisibleRows() {
        switch (craftingMonitor.getSize()) {
            case IGrid.SIZE_STRETCH:
                int screenSpaceAvailable = height - getTopHeight() - getBottomHeight() - tabs.getHeight();

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
        return getTopHeight() + (18 * getVisibleRows()) + 38 + tabs.getHeight();
    }

    @Override
    protected int getSideButtonYStart() {
        return super.getSideButtonYStart() + tabs.getHeight();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        tabs.actionPerformed(button);

        if (button == cancelButton && hasValidTabSelected()) {
            RS.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(craftingMonitor.getTabSelected().get()));
        } else if (button == cancelAllButton && tasks.size() > 0) {
            RS.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(null));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.tabs.mouseClicked();

        this.elementSelected = -1;

        if (mouseButton == 0) {
            int item = scrollbar != null ? scrollbar.getOffset() : 0;

            for (int i = 0; i < getVisibleRows(); ++i) {
                int ix = 8;
                int iy = 20 + (i * ITEM_HEIGHT) + tabs.getHeight();

                if (inBounds(ix, iy, ITEM_WIDTH, ITEM_HEIGHT, mouseX - guiLeft, mouseY - guiTop) && (item + i) < getElements().size()) {
                    this.elementSelected = item + i;
                }
            }
        }
    }
}
