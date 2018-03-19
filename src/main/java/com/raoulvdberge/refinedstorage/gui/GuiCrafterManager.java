package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCrafterManager;
import com.raoulvdberge.refinedstorage.container.ContainerCrafterManager;
import com.raoulvdberge.refinedstorage.container.slot.SlotCrafterManager;
import com.raoulvdberge.refinedstorage.gui.control.Scrollbar;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonGridSize;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.gui.control.TextFieldSearch;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;

import java.io.IOException;
import java.util.Map;

public class GuiCrafterManager extends GuiBase implements IResizableDisplay {
    private ContainerCrafterManager container;
    private NetworkNodeCrafterManager crafterManager;

    private TextFieldSearch searchField;

    public GuiCrafterManager(NetworkNodeCrafterManager crafterManager) {
        super(null, 193, 0);

        this.crafterManager = crafterManager;
    }

    public void setContainer(ContainerCrafterManager container) {
        this.container = container;
        this.inventorySlots = container;
    }

    @Override
    protected void calcHeight() {
        super.calcHeight();

        this.ySize = getTopHeight() + getBottomHeight() + (getVisibleRows() * 18);
        this.screenHeight = ySize;
    }

    @Override
    public int getTopHeight() {
        return 19;
    }

    @Override
    public int getBottomHeight() {
        return 99;
    }

    public int getVisibleRows() {
        switch (crafterManager.getSize()) {
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
        return (container == null || !crafterManager.isActive()) ? 0 : container.getRows();
    }

    @Override
    public int getCurrentOffset() {
        return scrollbar == null ? 0 : scrollbar.getOffset();
    }

    @Override
    public String getSearchFieldText() {
        return searchField == null ? "" : searchField.getText();
    }

    public int getYPlayerInventory() {
        return getTopHeight() + (getVisibleRows() * 18) + 16;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileCrafterManager.REDSTONE_MODE));
        addSideButton(new SideButtonGridSize(this, () -> crafterManager.getSize(), size -> TileDataManager.setParameter(TileCrafterManager.SIZE, size)));

        this.scrollbar = new Scrollbar(174, getTopHeight(), 12, (getVisibleRows() * 18) - 2);
        this.scrollbar.addListener((oldOffset, newOffset) -> {
            if (container != null) {
                container.initSlots(null);
            }
        });

        container.initSlots(null);

        int sx = x + 97 + 1;
        int sy = y + 6 + 1;

        if (searchField == null) {
            searchField = new TextFieldSearch(0, fontRenderer, sx, sy, 88 - 6);
            searchField.addListener(() -> container.initSlots(null));
        } else {
            searchField.x = sx;
            searchField.y = sy;
        }
    }

    @Override
    public void update(int x, int y) {
        scrollbar.setEnabled((getRows() - 1) >= getVisibleRows());
        scrollbar.setMaxOffset(getRows() - getVisibleRows());
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafter_manager.png");

        drawTexture(x, y, 0, 0, screenWidth, getTopHeight());

        int rows = getVisibleRows();

        int yy = y;

        for (int i = 0; i < rows; ++i) {
            yy += 18;

            drawTexture(x, yy, 0, getTopHeight() + (i > 0 ? (i == rows - 1 ? 18 * 2 : 18) : 0), screenWidth, 18);
        }

        yy += 18;

        drawTexture(x, yy, 0, getTopHeight() + (18 * 3), screenWidth, getBottomHeight());

        if (container != null && crafterManager.isActive()) {
            for (Slot slot : container.inventorySlots) {
                if (slot instanceof SlotCrafterManager && slot.isEnabled()) {
                    drawTexture(x + slot.xPos - 1, y + slot.yPos - 1, 0, 193, 18, 18);
                }
            }
        }

        if (searchField != null) {
            searchField.drawTextBox();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedButton);

        if (searchField != null) {
            searchField.mouseClicked(mouseX, mouseY, clickedButton);
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (searchField == null) {
            return;
        }

        if (checkHotbarKeys(keyCode)) {
            // NO OP
        } else if (searchField.textboxKeyTyped(character, keyCode)) {
            container.initSlots(null);
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:crafter_manager"));
        drawString(7, getYPlayerInventory() - 12, t("container.inventory"));

        if (container != null && crafterManager.isActive()) {
            for (Map.Entry<String, Integer> heading : container.getHeadings().entrySet()) {
                int y = heading.getValue();

                if (y >= getTopHeight() - 1 && y < getTopHeight() + getVisibleRows() * 18 - 1) {
                    GlStateManager.disableLighting();
                    GlStateManager.color(1, 1, 1);

                    bindTexture("gui/crafter_manager.png");

                    drawTexturedModalRect(7, y, 0, 174, 18 * 9, 18);

                    drawString(7 + 4, y + 6, RenderUtils.shorten(I18n.format(heading.getKey()), 25));
                }
            }
        }
    }
}
