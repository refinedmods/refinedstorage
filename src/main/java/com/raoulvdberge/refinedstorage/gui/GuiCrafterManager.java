package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCrafterManager;
import com.raoulvdberge.refinedstorage.container.ContainerCrafterManager;
import com.raoulvdberge.refinedstorage.gui.grid.IGridDisplay;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonGridSize;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.Map;

public class GuiCrafterManager extends GuiBase implements IGridDisplay {
    private ContainerCrafterManager container;
    private NetworkNodeCrafterManager crafterManager;
    private GuiTextField searchField;

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

        this.ySize = getHeader() + getFooter() + (getVisibleRows() * 18);
        this.screenHeight = ySize;
    }

    @Override
    public int getHeader() {
        return 19;
    }

    @Override
    public int getFooter() {
        return 99;
    }

    public int getVisibleRows() {
        switch (crafterManager.getSize()) {
            case IGrid.SIZE_STRETCH:
                int screenSpaceAvailable = height - getHeader() - getFooter();

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
        if (container == null || container.getContainerData() == null) {
            return 0;
        }

        int rows = 0;

        for (Map.Entry<String, Integer> containerData : container.getContainerData().entrySet()) {
            if (containerData.getKey().toLowerCase().contains(getSearchFieldText().toLowerCase())) {
                rows++;
                rows += containerData.getValue();
            }
        }

        return rows;
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
        return getHeader() + (getVisibleRows() * 18) + 16;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileCrafterManager.REDSTONE_MODE));
        addSideButton(new SideButtonGridSize(this, () -> crafterManager.getSize(), size -> TileDataManager.setParameter(TileCrafterManager.SIZE, size)));

        this.scrollbar = new Scrollbar(174, getHeader(), 12, (getVisibleRows() * 18) - 2);
        this.scrollbar.addListener((oldOffset, newOffset) -> {
            if (container != null) {
                container.initSlots(null);
            }
        });

        container.initSlots(null);

        int sx = x + 97 + 1;
        int sy = y + 6 + 1;

        if (searchField == null) {
            searchField = new GuiTextField(0, fontRenderer, sx, sy, 88 - 6, fontRenderer.FONT_HEIGHT);
            searchField.setEnableBackgroundDrawing(false);
            searchField.setVisible(true);
            searchField.setTextColor(16777215);
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

        drawTexture(x, y, 0, 0, screenWidth, getHeader());

        int rows = getVisibleRows();

        for (int i = 0; i < rows; ++i) {
            y += 18;

            drawTexture(x, y, 0, getHeader() + (i > 0 ? (i == rows - 1 ? 18 * 2 : 18) : 0), screenWidth, 18);
        }

        y += 18;

        drawTexture(x, y, 0, getHeader() + (18 * 3), screenWidth, getFooter());

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

        if (container != null) {
            int x = 7;
            int y = 18 - getCurrentOffset() * 18;

            for (Map.Entry<String, Integer> entry : container.getContainerData().entrySet()) {
                if (entry.getKey().toLowerCase().contains(getSearchFieldText().toLowerCase())) {
                    if (y >= getHeader() - 1 && y < getHeader() + getVisibleRows() * 18 - 1) {
                        GlStateManager.disableLighting();
                        GlStateManager.color(1, 1, 1);

                        bindTexture("gui/crafter_manager.png");

                        drawTexturedModalRect(x, y, 0, 174, 18 * 9, 18);

                        drawString(x + 4, y + 5, I18n.format(entry.getKey()));
                    }

                    y += (entry.getValue() + 1) * 18;
                }
            }
        }
    }


}
