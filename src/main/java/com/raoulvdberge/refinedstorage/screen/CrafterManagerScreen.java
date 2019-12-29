package com.raoulvdberge.refinedstorage.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode;
import com.raoulvdberge.refinedstorage.container.CrafterManagerContainer;
import com.raoulvdberge.refinedstorage.container.slot.CrafterManagerSlot;
import com.raoulvdberge.refinedstorage.screen.widget.ScrollbarWidget;
import com.raoulvdberge.refinedstorage.screen.widget.SearchWidget;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.CrafterManagerSearchBoxModeSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.GridSizeSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.raoulvdberge.refinedstorage.tile.CrafterManagerTile;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import yalter.mousetweaks.api.MouseTweaksDisableWheelTweak;

import java.util.Map;

@MouseTweaksDisableWheelTweak
public class CrafterManagerScreen extends BaseScreen<CrafterManagerContainer> implements IScreenInfoProvider {
    private CrafterManagerNetworkNode crafterManager;

    private ScrollbarWidget scrollbar;
    private SearchWidget searchField;

    public CrafterManagerScreen(CrafterManagerContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 193, 0, inventory, title);

        this.crafterManager = ((CrafterManagerTile) container.getTile()).getNode();
    }

    @Override
    protected void onPreInit() {
        this.ySize = getTopHeight() + getBottomHeight() + (getVisibleRows() * 18);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, CrafterManagerTile.REDSTONE_MODE));
        addSideButton(new CrafterManagerSearchBoxModeSideButton(this));
        addSideButton(new GridSizeSideButton(this, () -> crafterManager.getSize(), size -> TileDataManager.setParameter(CrafterManagerTile.SIZE, size)));

        this.scrollbar = new ScrollbarWidget(this, 174, getTopHeight(), 12, (getVisibleRows() * 18) - 2);
        this.scrollbar.addListener((oldOffset, newOffset) -> container.initSlots(null));

        container.initSlots(null);

        int sx = x + 97 + 1;
        int sy = y + 6 + 1;

        if (searchField == null) {
            searchField = new SearchWidget(font, sx, sy, 88 - 6);
            searchField.func_212954_a(value -> {
                searchField.updateJei();

                container.initSlots(null);
            });
            searchField.setMode(crafterManager.getSearchBoxMode());
        } else {
            searchField.x = sx;
            searchField.y = sy;
        }

        addButton(searchField);
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

        if (crafterManager.isActiveOnClient()) {
            for (Slot slot : container.inventorySlots) {
                if (slot instanceof CrafterManagerSlot && slot.isEnabled()) {
                    blit(x + slot.xPos - 1, y + slot.yPos - 1, 0, 193, 18, 18);
                }
            }
        }

        searchField.render(0, 0, 0);

        scrollbar.render();
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, title.getFormattedText());
        renderString(7, getYPlayerInventory() - 12, I18n.format("container.inventory"));

        if (container != null && crafterManager.isActiveOnClient()) {
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        if (scrollbar.mouseClicked(mouseX, mouseY, clickedButton)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (searchField.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            return true;
        }

        return super.charTyped(p_charTyped_1_, p_charTyped_2_);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (searchField.keyPressed(key, scanCode, modifiers) || searchField.func_212955_f()) {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
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

    public SearchWidget getSearchField() {
        return searchField;
    }

    public CrafterManagerNetworkNode getCrafterManager() {
        return crafterManager;
    }

    @Override
    public int getTopHeight() {
        return 19;
    }

    @Override
    public int getBottomHeight() {
        return 99;
    }

    @Override
    public int getVisibleRows() {
        switch (crafterManager.getSize()) {
            case IGrid.SIZE_STRETCH:
                int screenSpaceAvailable = height - getTopHeight() - getBottomHeight();

                return Math.max(3, Math.min((screenSpaceAvailable / 18) - 3, RS.CLIENT_CONFIG.getCrafterManager().getMaxRowsStretch()));
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
        return !crafterManager.isActiveOnClient() ? 0 : container.getRows();
    }

    @Override
    public int getCurrentOffset() {
        return scrollbar == null ? 0 : scrollbar.getOffset();
    }

    @Override
    public String getSearchFieldText() {
        return searchField == null ? "" : searchField.getText();
    }

    @Override
    public int getYPlayerInventory() {
        return getTopHeight() + (getVisibleRows() * 18) + 16;
    }
}
