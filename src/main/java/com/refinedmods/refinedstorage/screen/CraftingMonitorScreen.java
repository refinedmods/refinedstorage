package com.refinedmods.refinedstorage.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.refinedmods.refinedstorage.api.network.grid.IGridTab;
import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.api.render.IElementDrawers;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.render.CraftingMonitorElementDrawers;
import com.refinedmods.refinedstorage.apiimpl.render.ElementDrawers;
import com.refinedmods.refinedstorage.container.CraftingMonitorContainer;
import com.refinedmods.refinedstorage.network.craftingmonitor.CraftingMonitorCancelMessage;
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.screen.widget.TabListWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.ICraftingMonitor;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CraftingMonitorScreen extends BaseScreen<CraftingMonitorContainer> {
    private static final int ROWS = 5;
    private static final int ITEM_WIDTH = 73;
    private static final int ITEM_HEIGHT = 29;
    private final ScrollbarWidget scrollbar;
    private final ICraftingMonitor craftingMonitor;
    private final TabListWidget<CraftingMonitorContainer> tabs;
    private final IElementDrawers drawers = new CraftingMonitorElementDrawers(this, ITEM_WIDTH, ITEM_HEIGHT);
    private Button cancelButton;
    private Button cancelAllButton;
    private List<IGridTab> tasks = Collections.emptyList();

    public CraftingMonitorScreen(CraftingMonitorContainer container, Inventory inventory, Component title) {
        super(container, 254, 201, inventory, title);

        this.craftingMonitor = container.getCraftingMonitor();

        this.tabs = new TabListWidget<>(this, new ElementDrawers<>(this), () -> tasks, () -> (int) Math.floor((float) Math.max(0, tasks.size() - 1) / (float) ICraftingMonitor.TABS_PER_PAGE), craftingMonitor::getTabPage, () -> {
            IGridTab tab = getCurrentTab();

            if (tab == null) {
                return -1;
            }

            return tasks.indexOf(tab);
        }, ICraftingMonitor.TABS_PER_PAGE);
        this.tabs.addListener(new TabListWidget.ITabListListener() {
            @Override
            public void onSelectionChanged(int tab) {
                craftingMonitor.onTabSelectionChanged(Optional.of(((Task) tasks.get(tab)).id));

                scrollbar.setOffset(0);
            }

            @Override
            public void onPageChanged(int page) {
                craftingMonitor.onTabPageChanged(page);
            }
        });

        this.scrollbar = new ScrollbarWidget(this, 235, 20, 12, 149);
    }

    public void setTasks(List<IGridTab> tasks) {
        this.tasks = tasks;
    }

    public List<ICraftingMonitorElement> getElements() {
        if (!craftingMonitor.isActiveOnClient()) {
            return Collections.emptyList();
        }

        IGridTab tab = getCurrentTab();

        if (tab == null) {
            return Collections.emptyList();
        }

        return ((Task) tab).elements;
    }

    @Override
    public void onPostInit(int x, int y) {
        this.tabs.init(imageWidth);

        if (craftingMonitor.getRedstoneModeParameter() != null) {
            addSideButton(new RedstoneModeSideButton(this, craftingMonitor.getRedstoneModeParameter()));
        }

        Component cancel = new TranslatableComponent("gui.cancel");
        Component cancelAll = new TranslatableComponent("misc.refinedstorage.cancel_all");

        int cancelButtonWidth = 14 + font.width(cancel.getString());
        int cancelAllButtonWidth = 14 + font.width(cancelAll.getString());

        this.cancelButton = addButton(x + 7, y + 201 - 20 - 7, cancelButtonWidth, 20, cancel, false, true, btn -> {
            if (hasValidTabSelected()) {
                RS.NETWORK_HANDLER.sendToServer(new CraftingMonitorCancelMessage(((Task) getCurrentTab()).id));
            }
        });
        this.cancelAllButton = addButton(x + 7 + cancelButtonWidth + 4, y + 201 - 20 - 7, cancelAllButtonWidth, 20, cancelAll, false, true, btn -> {
            if (!tasks.isEmpty()) {
                RS.NETWORK_HANDLER.sendToServer(new CraftingMonitorCancelMessage(null));
            }
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
            cancelAllButton.active = !tasks.isEmpty();
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
        return tasks.stream().filter(t -> ((Task) t).id.equals(id)).findFirst().orElse(null);
    }

    @Override
    public void renderBackground(PoseStack matrixStack, int x, int y, int mouseX, int mouseY) {
        if (craftingMonitor.isActiveOnClient()) {
            tabs.drawBackground(matrixStack, x, y - tabs.getHeight());
        }

        bindTexture(RS.ID, "gui/crafting_preview.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);

        scrollbar.render(matrixStack);

        tabs.drawForeground(matrixStack, x, y - tabs.getHeight(), mouseX, mouseY, craftingMonitor.isActiveOnClient());
    }

    @Override
    public void renderForeground(PoseStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());

        int item = scrollbar != null ? scrollbar.getOffset() * 3 : 0;

        Lighting.setupFor3DItems();

        int x = 7;
        int y = 20;

        List<Component> tooltip = null;

        for (int i = 0; i < 3 * 5; ++i) {
            if (item < getElements().size()) {
                ICraftingMonitorElement element = getElements().get(item);

                element.draw(matrixStack, x, y, drawers);

                if (RenderUtils.inBounds(x, y, ITEM_WIDTH, ITEM_HEIGHT, mouseX, mouseY)) {
                    tooltip = element.getTooltip();
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

        if (tooltip != null && !tooltip.isEmpty()) {
            renderTooltip(matrixStack, ItemStack.EMPTY, mouseX, mouseY, tooltip);
        }

        tabs.drawTooltip(matrixStack, font, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        if (tabs.mouseClicked()) {
            return true;
        }

        if (scrollbar.mouseClicked(mouseX, mouseY, clickedButton)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override
    public void mouseMoved(double mx, double my) {
        scrollbar.mouseMoved(mx, my);

        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        return scrollbar.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        return this.scrollbar.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta);
    }

    public static class Task implements IGridTab {
        private final UUID id;
        private final ICraftingRequestInfo requested;
        private final int qty;
        private final long executionStarted;
        private final int completionPercentage;
        private final List<ICraftingMonitorElement> elements;

        public Task(UUID id, ICraftingRequestInfo requested, int qty, long executionStarted, int completionPercentage, List<ICraftingMonitorElement> elements) {
            this.id = id;
            this.requested = requested;
            this.qty = qty;
            this.executionStarted = executionStarted;
            this.completionPercentage = completionPercentage;
            this.elements = elements;
        }

        @Override
        public void drawTooltip(PoseStack matrixStack, int x, int y, Screen screen) {
            List<Component> lines = Lists.newArrayList(requested.getItem() != null ? requested.getItem().getHoverName() : requested.getFluid().getDisplayName());

            int totalSecs = (int) (System.currentTimeMillis() - executionStarted) / 1000;
            int hours = totalSecs / 3600;
            int minutes = (totalSecs % 3600) / 60;
            int seconds = totalSecs % 60;

            lines.add(new TranslatableComponent(
                "gui.refinedstorage.crafting_monitor.tooltip.requested",
                requested.getFluid() != null ? API.instance().getQuantityFormatter().formatInBucketForm(qty) : API.instance().getQuantityFormatter().format(qty)
            ).withStyle(ChatFormatting.GRAY));

            if (hours > 0) {
                lines.add(new TextComponent(String.format("%02d:%02d:%02d", hours, minutes, seconds)).withStyle(ChatFormatting.GRAY));
            } else {
                lines.add(new TextComponent(String.format("%02d:%02d", minutes, seconds)).withStyle(ChatFormatting.GRAY));
            }

            lines.add(new TextComponent(String.format("%d%%", completionPercentage)).withStyle(ChatFormatting.GRAY));

            screen.renderComponentTooltip(matrixStack, lines, x, y);
        }

        @Override
        public List<IFilter> getFilters() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void drawIcon(PoseStack matrixStack, int x, int y, IElementDrawer<ItemStack> itemDrawer, IElementDrawer<FluidStack> fluidDrawer) {
            if (requested.getItem() != null) {
                Lighting.setupFor3DItems();

                itemDrawer.draw(matrixStack, x, y, requested.getItem());
            } else {
                fluidDrawer.draw(matrixStack, x, y, requested.getFluid());
            }
        }
    }
}
