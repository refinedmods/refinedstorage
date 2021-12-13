package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode;
import com.refinedmods.refinedstorage.container.CrafterManagerContainer;
import com.refinedmods.refinedstorage.container.slot.CrafterManagerSlot;
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.screen.widget.SearchWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.CrafterManagerSearchBoxModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.GridSizeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.blockentity.CrafterManagerBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import yalter.mousetweaks.api.MouseTweaksDisableWheelTweak;

import java.util.Map;

@MouseTweaksDisableWheelTweak
public class CrafterManagerScreen extends BaseScreen<CrafterManagerContainer> implements IScreenInfoProvider {
    private final CrafterManagerNetworkNode crafterManager;

    private ScrollbarWidget scrollbar;
    private SearchWidget searchField;

    public CrafterManagerScreen(CrafterManagerContainer container, Inventory inventory, Component title) {
        super(container, 193, 0, inventory, title);

        this.crafterManager = ((CrafterManagerBlockEntity) container.getBlockEntity()).getNode();
    }

    @Override
    protected void onPreInit() {
        this.imageHeight = getTopHeight() + getBottomHeight() + (getVisibleRows() * 18);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
        addSideButton(new CrafterManagerSearchBoxModeSideButton(this));
        addSideButton(new GridSizeSideButton(this, crafterManager::getSize, size -> BlockEntitySynchronizationManager.setParameter(CrafterManagerBlockEntity.SIZE, size)));

        this.scrollbar = new ScrollbarWidget(this, 174, getTopHeight(), 12, (getVisibleRows() * 18) - 2);
        this.scrollbar.addListener((oldOffset, newOffset) -> menu.initSlots(null));

        menu.initSlots(null);

        int sx = x + 97 + 1;
        int sy = y + 6 + 1;

        if (searchField == null) {
            searchField = new SearchWidget(font, sx, sy, 88 - 6);
            searchField.setResponder(value -> {
                searchField.updateJei();

                menu.initSlots(null);
            });
            searchField.setMode(crafterManager.getSearchBoxMode());
        } else {
            searchField.x = sx;
            searchField.y = sy;
        }

        addRenderableWidget(searchField);
    }

    @Override
    public void tick(int x, int y) {
        scrollbar.setEnabled((getRows() - 1) >= getVisibleRows());
        scrollbar.setMaxOffset(getRows() - getVisibleRows());
    }

    @Override
    public void renderBackground(PoseStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/crafter_manager.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, getTopHeight());

        int rows = getVisibleRows();

        int yy = y;

        for (int i = 0; i < rows; ++i) {
            yy += 18;

            int yTextureStart = getTopHeight();
            if (i > 0) {
                if (i == rows - 1) {
                    yTextureStart += 18 * 2;
                } else {
                    yTextureStart += 18;
                }
            }

            blit(matrixStack, x, yy, 0, yTextureStart, imageWidth, 18);
        }

        yy += 18;

        blit(matrixStack, x, yy, 0, getTopHeight() + (18 * 3), imageWidth, getBottomHeight());

        if (crafterManager.isActiveOnClient()) {
            for (Slot slot : menu.slots) {
                if (slot instanceof CrafterManagerSlot && slot.isActive()) {
                    blit(matrixStack, x + slot.x - 1, y + slot.y - 1, 0, 193, 18, 18);
                }
            }
        }

        searchField.render(matrixStack, 0, 0, 0);

        scrollbar.render(matrixStack);
    }

    @Override
    public void renderForeground(PoseStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, getYPlayerInventory() - 12, I18n.get("container.inventory"));

        if (menu != null && crafterManager.isActiveOnClient()) {
            for (Map.Entry<String, Integer> heading : menu.getHeadings().entrySet()) {
                int y = heading.getValue();

                if (y >= getTopHeight() - 1 && y < getTopHeight() + getVisibleRows() * 18 - 1) {
                    RenderSystem.setShaderColor(1, 1, 1, 1);

                    bindTexture(RS.ID, "gui/crafter_manager.png");

                    blit(matrixStack, 7, y, 0, 174, 18 * 9, 18);

                    renderString(matrixStack, 7 + 4, y + 6, RenderUtils.shorten(I18n.get(heading.getKey()), 25));
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
    public boolean charTyped(char unknown1, int unknown2) {
        if (searchField.charTyped(unknown1, unknown2)) {
            return true;
        }

        return super.charTyped(unknown1, unknown2);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (searchField.keyPressed(key, scanCode, modifiers) || searchField.canConsumeInput()) {
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
        return !crafterManager.isActiveOnClient() ? 0 : menu.getRows();
    }

    @Override
    public int getCurrentOffset() {
        return scrollbar == null ? 0 : scrollbar.getOffset();
    }

    @Override
    public String getSearchFieldText() {
        return searchField == null ? "" : searchField.getValue();
    }

    @Override
    public int getYPlayerInventory() {
        return getTopHeight() + (getVisibleRows() * 18) + 16;
    }
}
