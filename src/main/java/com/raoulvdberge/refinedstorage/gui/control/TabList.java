package com.raoulvdberge.refinedstorage.gui.control;

import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class TabList {
    public interface ITabListListener {
        void onSelectionChanged(int tab);

        void onPageChanged(int page);
    }

    private GuiBase gui;
    private GuiBase.ElementDrawers drawers;

    private Supplier<List<IGridTab>> tabs;
    private int tabHovering;
    private int tabsPerPage;
    private Supplier<Integer> pages;
    private Supplier<Integer> page;
    private Supplier<Integer> selected;
    private boolean hadTabs;

    private List<ITabListListener> listeners = new LinkedList<>();

    private GuiButton left;
    private GuiButton right;

    private int width;

    public TabList(GuiBase gui, GuiBase.ElementDrawers drawers, Supplier<List<IGridTab>> tabs, Supplier<Integer> pages, Supplier<Integer> page, Supplier<Integer> selected, int tabsPerPage) {
        this.gui = gui;
        this.drawers = drawers;
        this.tabs = tabs;
        this.pages = pages;
        this.page = page;
        this.selected = selected;
        this.tabsPerPage = tabsPerPage;
    }

    public void init(int width) {
        this.width = width;
        this.left = gui.addButton(gui.getGuiLeft(), gui.getGuiTop() - 22, 20, 20, "<", true, pages.get() > 0);
        this.right = gui.addButton(gui.getGuiLeft() + width - 22, gui.getGuiTop() - 22, 20, 20, ">", true, pages.get() > 0);
    }

    public void addListener(ITabListListener listener) {
        listeners.add(listener);
    }

    public void drawForeground(int x, int y, int mouseX, int mouseY) {
        this.tabHovering = -1;

        int j = 0;
        for (int i = page.get() * tabsPerPage; i < (page.get() * tabsPerPage) + tabsPerPage; ++i) {
            if (i < tabs.get().size()) {
                drawTab(tabs.get().get(i), true, x, y, i, j);

                if (gui.inBounds(x + ((IGridTab.TAB_WIDTH + 1) * j), y, IGridTab.TAB_WIDTH, IGridTab.TAB_HEIGHT - (i == selected.get() ? 2 : 7), mouseX, mouseY)) {
                    this.tabHovering = i;
                }

                j++;
            }
        }
    }

    public void update() {
        boolean hasTabs = !tabs.get().isEmpty();

        if (this.hadTabs != hasTabs) {
            this.hadTabs = hasTabs;

            gui.initGui();
        }

        if (page.get() > pages.get()) {
            listeners.forEach(t -> t.onPageChanged(pages.get()));
        }

        left.visible = pages.get() > 0;
        right.visible = pages.get() > 0;
        left.enabled = page.get() > 0;
        right.enabled = page.get() < pages.get();
    }

    public void drawBackground(int x, int y) {
        int j = 0;
        for (int i = page.get() * tabsPerPage; i < (page.get() * tabsPerPage) + tabsPerPage; ++i) {
            if (i < tabs.get().size()) {
                drawTab(tabs.get().get(i), false, x, y, i, j++);
            }
        }
    }

    public int getHeight() {
        return !tabs.get().isEmpty() ? IGridTab.TAB_HEIGHT - 4 : 0;
    }

    private void drawTab(IGridTab tab, boolean foregroundLayer, int x, int y, int index, int num) {
        boolean isSelected = index == selected.get();

        if ((foregroundLayer && !isSelected) || (!foregroundLayer && isSelected)) {
            return;
        }

        int tx = x + ((IGridTab.TAB_WIDTH + 1) * num);
        int ty = y;

        GlStateManager.enableAlpha();

        gui.bindTexture("icons.png");

        if (!isSelected) {
            ty += 3;
        }

        int uvx;
        int uvy = 225;
        int tbw = IGridTab.TAB_WIDTH;
        int otx = tx;

        if (isSelected) {
            uvx = 227;

            if (num > 0) {
                uvx = 226;
                uvy = 194;
                tbw++;
                tx--;
            }
        } else {
            uvx = 199;
        }

        gui.drawTexture(tx, ty, uvx, uvy, tbw, IGridTab.TAB_HEIGHT);

        tab.drawIcon(otx + 6, ty + 9 - (!isSelected ? 3 : 0), drawers.getItemDrawer(), drawers.getFluidDrawer());
    }

    public void drawTooltip(FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (tabHovering >= 0 && tabHovering < tabs.get().size()) {
            tabs.get().get(tabHovering).drawTooltip(mouseX, mouseY, gui.getScreenWidth(), gui.getScreenHeight(), fontRenderer);
        }

        if (pages.get() > 0) {
            String text = (page.get() + 1) + " / " + (pages.get() + 1);

            gui.drawString((int) ((width - (float) fontRenderer.getStringWidth(text)) / 2F), -16, text, 0xFFFFFF);
        }
    }

    public void mouseClicked() {
        if (tabHovering >= 0 && tabHovering < tabs.get().size()) {
            listeners.forEach(t -> t.onSelectionChanged(tabHovering));
        }
    }

    public void actionPerformed(GuiButton button) {
        if (button == left) {
            listeners.forEach(t -> t.onPageChanged(page.get() - 1));
        } else if (button == right) {
            listeners.forEach(t -> t.onPageChanged(page.get() + 1));
        }
    }
}
