package com.raoulvdberge.refinedstorage.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawer;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.render.CraftingMonitorElementDrawers;
import com.raoulvdberge.refinedstorage.apiimpl.render.ElementDrawers;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.gui.widget.ScrollbarWidget;
import com.raoulvdberge.refinedstorage.gui.widget.TabListWidget;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GuiCraftingMonitor extends GuiBase<ContainerCraftingMonitor> {
    public static class CraftingMonitorTask implements IGridTab {
        private UUID id;
        private ICraftingRequestInfo requested;
        private int qty;
        private long executionStarted;
        private int completionPercentage;
        private List<ICraftingMonitorElement> elements;

        public CraftingMonitorTask(UUID id, ICraftingRequestInfo requested, int qty, long executionStarted, int completionPercentage, List<ICraftingMonitorElement> elements) {
            this.id = id;
            this.requested = requested;
            this.qty = qty;
            this.executionStarted = executionStarted;
            this.completionPercentage = completionPercentage;
            this.elements = elements;
        }

        @Override
        public List<IFilter> getFilters() {
            return null;
        }

        @Override
        public void drawTooltip(int x, int y, int xSize, int ySize, FontRenderer fontRenderer) {
            List<String> textLines = Lists.newArrayList(requested.getItem() != null ? requested.getItem().getDisplayName().getFormattedText() : requested.getFluid().getDisplayName().getFormattedText()); // TODO
            List<String> smallTextLines = Lists.newArrayList();

            int totalSecs = (int) (System.currentTimeMillis() - executionStarted) / 1000;
            int minutes = (totalSecs % 3600) / 60;
            int seconds = totalSecs % 60;

            smallTextLines.add(I18n.format("gui.refinedstorage:crafting_monitor.tooltip.requested", requested.getFluid() != null ? API.instance().getQuantityFormatter().formatInBucketForm(qty) : API.instance().getQuantityFormatter().format(qty)));
            smallTextLines.add(String.format("%02d:%02d", minutes, seconds));
            smallTextLines.add(String.format("%d%%", completionPercentage));

            RenderUtils.drawTooltipWithSmallText(textLines, smallTextLines, true, ItemStack.EMPTY, x, y, xSize, ySize, fontRenderer);
        }

        @Override
        public void drawIcon(int x, int y, IElementDrawer<ItemStack> itemDrawer, IElementDrawer<FluidStack> fluidDrawer) {
            if (requested.getItem() != null) {
                RenderHelper.enableGUIStandardItemLighting();

                itemDrawer.draw(x, y, requested.getItem());
            } else {
                fluidDrawer.draw(x, y, requested.getFluid());

                GlStateManager.enableAlphaTest();
            }
        }
    }

    private static final int ROWS = 5;

    private static final int ITEM_WIDTH = 73;
    private static final int ITEM_HEIGHT = 29;

    private Button cancelButton;
    private Button cancelAllButton;

    private ScrollbarWidget scrollbar;

    private ICraftingMonitor craftingMonitor;

    private List<IGridTab> tasks = Collections.emptyList();
    private TabListWidget tabs;

    private IElementDrawers drawers = new CraftingMonitorElementDrawers(this, font, ITEM_WIDTH, ITEM_HEIGHT);

    public GuiCraftingMonitor(ContainerCraftingMonitor container, ICraftingMonitor craftingMonitor, PlayerInventory inventory) {
        super(container, 254, 201, inventory, null);

        this.craftingMonitor = craftingMonitor;

        this.tabs = new TabListWidget(this, new ElementDrawers(this, font), () -> tasks, () -> (int) Math.floor((float) Math.max(0, tasks.size() - 1) / (float) ICraftingMonitor.TABS_PER_PAGE), craftingMonitor::getTabPage, () -> {
            IGridTab tab = getCurrentTab();

            if (tab == null) {
                return -1;
            }

            return tasks.indexOf(tab);
        }, ICraftingMonitor.TABS_PER_PAGE);

        this.tabs.addListener(new TabListWidget.ITabListListener() {
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
        if (!craftingMonitor.isActive()) {
            return Collections.emptyList();
        }

        IGridTab tab = getCurrentTab();

        if (tab == null) {
            return Collections.emptyList();
        }

        return ((CraftingMonitorTask) tab).elements;
    }

    @Override
    public void init(int x, int y) {
        this.tabs.init(xSize);

        this.scrollbar = new ScrollbarWidget(235, 20, 12, 149);

        if (craftingMonitor.getRedstoneModeParameter() != null) {
            addSideButton(new SideButtonRedstoneMode(this, craftingMonitor.getRedstoneModeParameter()));
        }

        String cancel = I18n.format("gui.cancel");
        String cancelAll = I18n.format("misc.refinedstorage:cancel_all");

        int cancelButtonWidth = 14 + font.getStringWidth(cancel);
        int cancelAllButtonWidth = 14 + font.getStringWidth(cancelAll);

        this.cancelButton = addButton(x + 7, y + 201 - 20 - 7, cancelButtonWidth, 20, cancel, false, true, btn -> {
        });
        this.cancelAllButton = addButton(x + 7 + cancelButtonWidth + 4, y + 201 - 20 - 7, cancelAllButtonWidth, 20, cancelAll, false, true, btn -> {
        });
    }

    private void updateScrollbar() {
        if (scrollbar != null) {
            scrollbar.setEnabled(getRows() > ROWS);
            scrollbar.setMaxOffset(getRows() - ROWS);
        }
    }

    private int getRows() {
        return Math.max(0, (int) Math.ceil((float) getElements().size() / 3F));
    }

    @Override
    public void tick(int x, int y) {
        updateScrollbar();

        this.tabs.update();

        if (cancelButton != null) {
            cancelButton.active = hasValidTabSelected();
        }

        if (cancelAllButton != null) {
            cancelAllButton.active = tasks.size() > 0;
        }
    }

    private boolean hasValidTabSelected() {
        return getCurrentTab() != null;
    }

    @Nullable
    private IGridTab getCurrentTab() {
        Optional<UUID> currentTab = craftingMonitor.getTabSelected();

        if (currentTab.isPresent()) {
            IGridTab tab = getTabById(currentTab.get());

            if (tab != null) {
                return tab;
            }
        }

        if (tasks.isEmpty()) {
            return null;
        }

        return tasks.get(0);
    }

    @Nullable
    private IGridTab getTabById(UUID id) {
        return tasks.stream().filter(t -> ((CraftingMonitorTask) t).id.equals(id)).findFirst().orElse(null);
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        if (craftingMonitor.isActive()) {
            tabs.drawBackground(x, y - tabs.getHeight());
        }

        bindTexture("gui/crafting_preview.png");

        blit(x, y, 0, 0, xSize, ySize);

        tabs.drawForeground(x, y - tabs.getHeight(), mouseX, mouseY, craftingMonitor.isActive());
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format(craftingMonitor.getGuiTitle()));

        int item = scrollbar != null ? scrollbar.getOffset() * 3 : 0;

        RenderHelper.enableGUIStandardItemLighting();

        int x = 7;
        int y = 20;

        String itemSelectedTooltip = null;

        for (int i = 0; i < 3 * 5; ++i) {
            if (item < getElements().size()) {
                ICraftingMonitorElement element = getElements().get(item);

                element.draw(x, y, drawers);

                if (inBounds(x, y, ITEM_WIDTH, ITEM_HEIGHT, mouseX, mouseY)) {
                    itemSelectedTooltip = element.getTooltip();
                }

                if ((i + 1) % 3 == 0) {
                    x = 7;
                    y += 30;
                } else {
                    x += 74;
                }
            }

            item++;
        }

        if (itemSelectedTooltip != null && !itemSelectedTooltip.isEmpty()) {
            renderTooltip(mouseX, mouseY, I18n.format(itemSelectedTooltip));
        }

        tabs.drawTooltip(font, mouseX, mouseY);
    }

    /* TODO
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        tabs.actionPerformed(button);

        if (button == cancelButton && hasValidTabSelected()) {
            RS.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(((CraftingMonitorTask) getCurrentTab()).id));
        } else if (button == cancelAllButton && tasks.size() > 0) {
            RS.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(null));
        }
    }*/

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        return this.tabs.mouseClicked();
    }
}
