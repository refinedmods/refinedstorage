package com.refinedmods.refinedstorage.screen.widget;

import com.refinedmods.refinedstorage.api.network.grid.IGridTab;
import com.refinedmods.refinedstorage.apiimpl.render.ElementDrawers;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class TabListWidget<T extends AbstractContainerMenu> {
    private final BaseScreen<?> screen;
    private final ElementDrawers<T> drawers;
    private final Supplier<List<IGridTab>> tabs;
    private final int tabsPerPage;
    private final Supplier<Integer> pages;
    private final Supplier<Integer> page;
    private final Supplier<Integer> selected;
    private final List<ITabListListener> listeners = new LinkedList<>();
    private int tabHovering;
    private boolean hadTabs;
    private Button left;
    private Button right;

    public TabListWidget(BaseScreen<T> screen, ElementDrawers<T> drawers, Supplier<List<IGridTab>> tabs, Supplier<Integer> pages, Supplier<Integer> page, Supplier<Integer> selected, int tabsPerPage) {
        this.screen = screen;
        this.drawers = drawers;
        this.tabs = tabs;
        this.pages = pages;
        this.page = page;
        this.selected = selected;
        this.tabsPerPage = tabsPerPage;
    }

    public void init(int width) {
        this.left = screen.addButton(screen.getGuiLeft(), screen.getGuiTop() - 22, 20, 20, Component.literal("<"), true, pages.get() > 0, btn -> listeners.forEach(t -> t.onPageChanged(page.get() - 1)));
        this.right = screen.addButton(screen.getGuiLeft() + width - 22, screen.getGuiTop() - 22, 20, 20, Component.literal(">"), true, pages.get() > 0, btn -> listeners.forEach(t -> t.onPageChanged(page.get() + 1)));
    }

    public void addListener(ITabListListener listener) {
        listeners.add(listener);
    }

    public void drawForeground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, boolean visible) {
        this.tabHovering = -1;

        if (visible) {
            int j = 0;
            for (int i = page.get() * tabsPerPage; i < (page.get() * tabsPerPage) + tabsPerPage; ++i) {
                if (i < tabs.get().size()) {
                    drawTab(graphics, tabs.get().get(i), true, x, y, i, j);

                    if (RenderUtils.inBounds(x + getXOffset() + ((IGridTab.TAB_WIDTH + 1) * j), y, IGridTab.TAB_WIDTH, IGridTab.TAB_HEIGHT - (i == selected.get() ? 2 : 7), mouseX, mouseY)) {
                        this.tabHovering = i;
                    }

                    j++;
                }
            }
        }
    }

    public void update() {
        boolean hasTabs = !tabs.get().isEmpty();

        if (this.hadTabs != hasTabs) {
            this.hadTabs = hasTabs;

            screen.init();
        }

        if (page.get() > pages.get()) {
            listeners.forEach(t -> t.onPageChanged(pages.get()));
        }

        left.visible = pages.get() > 0;
        right.visible = pages.get() > 0;

        left.active = page.get() > 0;
        right.active = page.get() < pages.get();
    }

    public void drawBackground(GuiGraphics graphics, int x, int y) {
        int j = 0;
        for (int i = page.get() * tabsPerPage; i < (page.get() * tabsPerPage) + tabsPerPage; ++i) {
            if (i < tabs.get().size()) {
                drawTab(graphics, tabs.get().get(i), false, x, y, i, j++);
            }
        }
    }

    public int getHeight() {
        return !tabs.get().isEmpty() ? IGridTab.TAB_HEIGHT - 4 : 0;
    }

    private int getXOffset() {
        if (pages.get() > 0) {
            return 24;
        }

        return 0;
    }

    private void drawTab(GuiGraphics graphics, IGridTab tab, boolean foregroundLayer, int x, int y, int index, int num) {
        boolean isSelected = index == selected.get();

        if ((foregroundLayer && !isSelected) || (!foregroundLayer && isSelected)) {
            return;
        }

        int tx = x + getXOffset() + ((IGridTab.TAB_WIDTH + 1) * num);
        int ty = y;

        if (!isSelected) {
            ty += 3;
        }

        int uvx;
        int uvy = 225;
        int tbw = IGridTab.TAB_WIDTH;
        int otx = tx;

        if (isSelected) {
            uvx = 227;

            if (num > 0 || getXOffset() != 0) {
                uvx = 226;
                uvy = 194;
                tbw++;
                tx--;
            }
        } else {
            uvx = 199;
        }

        graphics.blit(BaseScreen.ICONS_TEXTURE, tx, ty, uvx, uvy, tbw, IGridTab.TAB_HEIGHT);

        tab.drawIcon(graphics, otx + 6, ty + 9 - (!isSelected ? 3 : 0), drawers.getItemDrawer(), drawers.getFluidDrawer());
    }

    public void drawTooltip(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        if (tabHovering >= 0 && tabHovering < tabs.get().size()) {
            tabs.get().get(tabHovering).drawTooltip(font, graphics, mouseX, mouseY);
        }
    }

    public boolean mouseClicked() {
        if (tabHovering >= 0 && tabHovering < tabs.get().size()) {
            listeners.forEach(t -> t.onSelectionChanged(tabHovering));

            return true;
        }

        return false;
    }

    public interface ITabListListener {
        void onSelectionChanged(int tab);

        void onPageChanged(int page);
    }
}
