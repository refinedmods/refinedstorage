package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawer;
import com.raoulvdberge.refinedstorage.api.render.IElementDrawers;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.network.MessageCraftingMonitorCancel;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GuiCraftingMonitor extends GuiBase {
    public static List<ICraftingMonitorElement> ELEMENTS = Collections.emptyList();

    public class CraftingMonitorElementDrawers extends ElementDrawers {
        private IElementDrawer<Integer> overlayDrawer = (x, y, colour) -> {
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableLighting();
            drawRect(x, y, x + ITEM_WIDTH, y + ITEM_HEIGHT - 1, colour);
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

    private IElementDrawers drawers = new CraftingMonitorElementDrawers();

    private int itemSelected = -1;

    private int itemSelectedX = -1;
    private int itemSelectedY = -1;

    public GuiCraftingMonitor(ContainerCraftingMonitor container, ICraftingMonitor craftingMonitor) {
        super(container, 176, 230);

        this.craftingMonitor = craftingMonitor;
        this.scrollbar = new Scrollbar(157, 20, 12, 89);
    }

    public List<ICraftingMonitorElement> getElements() {
        return craftingMonitor.isConnected() ? ELEMENTS : Collections.emptyList();
    }

    @Override
    public void init(int x, int y) {
        if (!(craftingMonitor instanceof WirelessCraftingMonitor)) {
            addSideButton(new SideButtonRedstoneMode(this, TileCraftingMonitor.REDSTONE_MODE));
        }

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

        cancelButton.enabled = itemSelected != -1 && getElements().get(itemSelected).getTaskId() != -1;
        cancelAllButton.enabled = getElements().size() > 0;
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafting_monitor.png");

        drawTexture(x, y, 0, 0, width, height);

        if (itemSelectedX != -1 &&
            itemSelectedY != -1 &&
            itemSelected >= 0 &&
            itemSelected < getElements().size() &&
            getElements().get(itemSelected).canDrawSelection()) {
            drawTexture(x + itemSelectedX, y + itemSelectedY, 0, 232, ITEM_WIDTH, ITEM_HEIGHT);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:crafting_monitor"));
        drawString(7, 137, t("container.inventory"));

        int item = scrollbar.getOffset();

        RenderHelper.enableGUIStandardItemLighting();

        int x = 8;
        int y = 20;

        itemSelectedX = -1;
        itemSelectedY = -1;
        String itemSelectedTooltip = null;

        for (int i = 0; i < VISIBLE_ROWS; ++i) {
            if (item < getElements().size()) {
                ICraftingMonitorElement element = getElements().get(item);

                if (item == itemSelected) {
                    itemSelectedX = x;
                    itemSelectedY = y;
                }

                if (inBounds(x, y, ITEM_WIDTH, ITEM_HEIGHT, mouseX, mouseY)) {
                    itemSelectedTooltip = element.getTooltip();
                }

                element.draw(x, y, drawers);

                y += ITEM_HEIGHT;
            }

            item++;
        }

        if (itemSelectedTooltip != null && !itemSelectedTooltip.isEmpty()) {
            drawTooltip(mouseX, mouseY, I18n.format(itemSelectedTooltip));
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
                RS.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(element.getTaskId()));
            }
        } else if (button == cancelAllButton && getElements().size() > 0) {
            RS.INSTANCE.network.sendToServer(new MessageCraftingMonitorCancel(-1));
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
}
