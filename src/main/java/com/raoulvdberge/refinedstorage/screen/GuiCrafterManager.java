package com.raoulvdberge.refinedstorage.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCrafterManager;
import com.raoulvdberge.refinedstorage.container.CrafterManagerContainer;
import com.raoulvdberge.refinedstorage.container.slot.CrafterManagerSlot;
import com.raoulvdberge.refinedstorage.screen.widget.ScrollbarWidget;
import com.raoulvdberge.refinedstorage.screen.widget.SearchWidget;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonCrafterManagerSearchBoxMode;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonGridSize;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;

import java.util.Map;

// TODO @MouseTweaksDisableWheelTweak
public class GuiCrafterManager extends BaseScreen<CrafterManagerContainer> implements IResizableDisplay {
    private CrafterManagerContainer container;
    private NetworkNodeCrafterManager crafterManager;

    private ScrollbarWidget scrollbar;
    private SearchWidget searchField;

    public GuiCrafterManager(NetworkNodeCrafterManager crafterManager, PlayerInventory inventory) {
        super(null, 193, 0, inventory, null);

        this.crafterManager = crafterManager;
    }

    public NetworkNodeCrafterManager getCrafterManager() {
        return crafterManager;
    }

    public void setContainer(CrafterManagerContainer container) {
        this.container = container;
    }

    /* TODO
    @Override
    protected void calcHeight() {
        this.ySize = getTopHeight() + getBottomHeight() + (getVisibleRows() * 18);
        this.ySize = ySize;
    }*/

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
        addSideButton(new SideButtonCrafterManagerSearchBoxMode(this));
        addSideButton(new SideButtonGridSize(this, () -> crafterManager.getSize(), size -> TileDataManager.setParameter(TileCrafterManager.SIZE, size)));

        this.scrollbar = new ScrollbarWidget(this, 174, getTopHeight(), 12, (getVisibleRows() * 18) - 2);
        this.scrollbar.addListener((oldOffset, newOffset) -> {
            if (container != null) {
                container.initSlots(null);
            }
        });

        container.initSlots(null);

        int sx = x + 97 + 1;
        int sy = y + 6 + 1;

        if (searchField == null) {
            searchField = new SearchWidget(font, sx, sy, 88 - 6);
            searchField.addListener(() -> container.initSlots(null));
            searchField.setMode(crafterManager.getSearchBoxMode());
        } else {
            searchField.x = sx;
            searchField.y = sy;
        }
    }

    @Override
    public void tick(int x, int y) {
        scrollbar.setEnabled((getRows() - 1) >= getVisibleRows());
        scrollbar.setMaxOffset(getRows() - getVisibleRows());
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/crafter_manager.png");

        blit(x, y, 0, 0, xSize, getTopHeight());

        int rows = getVisibleRows();

        int yy = y;

        for (int i = 0; i < rows; ++i) {
            yy += 18;

            blit(x, yy, 0, getTopHeight() + (i > 0 ? (i == rows - 1 ? 18 * 2 : 18) : 0), xSize, 18);
        }

        yy += 18;

        blit(x, yy, 0, getTopHeight() + (18 * 3), xSize, getBottomHeight());

        if (container != null && crafterManager.isActive()) {
            for (Slot slot : container.inventorySlots) {
                if (slot instanceof CrafterManagerSlot && slot.isEnabled()) {
                    blit(x + slot.xPos - 1, y + slot.yPos - 1, 0, 193, 18, 18);
                }
            }
        }

        if (searchField != null) {
            // TODO render searchField.render();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        boolean clicked = searchField.mouseClicked(mouseX, mouseY, clickedButton);

        if (clicked) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    /* TODO
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
    }*/

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:crafter_manager"));
        renderString(7, getYPlayerInventory() - 12, I18n.format("container.inventory"));

        if (container != null && crafterManager.isActive()) {
            for (Map.Entry<String, Integer> heading : container.getHeadings().entrySet()) {
                int y = heading.getValue();

                if (y >= getTopHeight() - 1 && y < getTopHeight() + getVisibleRows() * 18 - 1) {
                    GlStateManager.disableLighting();
                    GlStateManager.color3f(1, 1, 1);

                    bindTexture(RS.ID, "gui/crafter_manager.png");

                    blit(7, y, 0, 174, 18 * 9, 18);

                    renderString(7 + 4, y + 6, RenderUtils.shorten(I18n.format(heading.getKey()), 25));
                }
            }
        }
    }

    public SearchWidget getSearchField() {
        return searchField;
    }
}
