package com.raoulvdberge.refinedstorage.screen.grid;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSKeyBindings;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.render.ElementDrawers;
import com.raoulvdberge.refinedstorage.container.GridContainer;
import com.raoulvdberge.refinedstorage.network.grid.*;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.IScreenInfoProvider;
import com.raoulvdberge.refinedstorage.screen.grid.sorting.*;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.screen.grid.stack.ItemGridStack;
import com.raoulvdberge.refinedstorage.screen.grid.view.FluidGridView;
import com.raoulvdberge.refinedstorage.screen.grid.view.IGridView;
import com.raoulvdberge.refinedstorage.screen.grid.view.ItemGridView;
import com.raoulvdberge.refinedstorage.screen.widget.ScrollbarWidget;
import com.raoulvdberge.refinedstorage.screen.widget.SearchWidget;
import com.raoulvdberge.refinedstorage.screen.widget.TabListWidget;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.grid.GridTile;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGridTile;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import com.raoulvdberge.refinedstorage.util.TimeUtils;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.List;

public class GridScreen extends BaseScreen<GridContainer> implements IScreenInfoProvider {
    private IGridView view;

    private SearchWidget searchField;
    private GuiCheckBox exactPattern;
    private GuiCheckBox processingPattern;

    private ScrollbarWidget scrollbar;

    private IGrid grid;
    private TabListWidget tabs;

    private boolean wasConnected;
    private boolean doSort;

    private int slotNumber;

    public GridScreen(GridContainer container, IGrid grid, PlayerInventory inventory, ITextComponent title) {
        super(container, 227, 0, inventory, title);

        this.grid = grid;
        this.view = grid.getGridType() == GridType.FLUID ? new FluidGridView(this, getDefaultSorter(), getSorters()) : new ItemGridView(this, getDefaultSorter(), getSorters());
        this.wasConnected = this.grid.isActive();
        this.tabs = new TabListWidget(this, new ElementDrawers(this, font), grid::getTabs, grid::getTotalTabPages, grid::getTabPage, grid::getTabSelected, IGrid.TABS_PER_PAGE);
        this.tabs.addListener(new TabListWidget.ITabListListener() {
            @Override
            public void onSelectionChanged(int tab) {
                grid.onTabSelectionChanged(tab);
            }

            @Override
            public void onPageChanged(int page) {
                grid.onTabPageChanged(page);
            }
        });
    }

    @Override
    protected void onPreInit() {
        super.onPreInit();
        this.doSort = true;
        this.ySize = getTopHeight() + getBottomHeight() + (getVisibleRows() * 18);
    }

    @Override
    public void onPostInit(int x, int y) {
        this.container.initSlots();

        this.tabs.init(xSize - 32);

        this.scrollbar = new ScrollbarWidget(this, 174, getTopHeight(), 12, (getVisibleRows() * 18) - 2);

        if (grid instanceof GridNetworkNode || grid instanceof PortableGridTile) {
            addSideButton(new RedstoneModeSideButton(this, grid instanceof GridNetworkNode ? GridTile.REDSTONE_MODE : PortableGridTile.REDSTONE_MODE));
        }

        int sx = x + 80 + 1;
        int sy = y + 6 + 1;

        if (searchField == null) {
            searchField = new SearchWidget(font, sx, sy, 88 - 6);
            searchField.func_212954_a(value -> {
                searchField.updateJei();

                getView().sort(); // Use getter since this view can be replaced.
            });
            searchField.setMode(grid.getSearchBoxMode());
        } else {
            searchField.x = sx;
            searchField.y = sy;
        }

        addButton(searchField);

        if (grid.getViewType() != -1) {
            addSideButton(new GridViewTypeSideButton(this, grid));
        }

        addSideButton(new GridSortingDirectionSideButton(this, grid));
        addSideButton(new GridSortingTypeSideButton(this, grid));
        addSideButton(new GridSearchBoxModeSideButton(this));
        addSideButton(new GridSizeSideButton(this, () -> grid.getSize(), size -> grid.onSizeChanged(size)));

        if (grid.getGridType() == GridType.PATTERN) {
            processingPattern = addCheckBox(x + 7, y + getTopHeight() + (getVisibleRows() * 18) + 60, I18n.format("misc.refinedstorage.processing"), GridTile.PROCESSING_PATTERN.getValue(), btn -> {
                // Rebuild the inventory slots before the slot change packet arrives.
                GridTile.PROCESSING_PATTERN.setValue(false, processingPattern.isChecked());
                ((GridNetworkNode) grid).clearMatrix(); // The server does this but let's do it earlier so the client doesn't notice.
                this.container.initSlots();

                TileDataManager.setParameter(GridTile.PROCESSING_PATTERN, processingPattern.isChecked());
            });

            if (!processingPattern.isChecked()) {
                exactPattern = addCheckBox(processingPattern.x + processingPattern.getWidth() + 5, y + getTopHeight() + (getVisibleRows() * 18) + 60, I18n.format("misc.refinedstorage.exact"), GridTile.EXACT_PATTERN.getValue(), btn -> TileDataManager.setParameter(GridTile.EXACT_PATTERN, exactPattern.isChecked()));
            }

            addSideButton(new TypeSideButton(this, GridTile.PROCESSING_TYPE));
        }

        updateScrollbar();
    }

    public IGrid getGrid() {
        return grid;
    }

    public void setView(IGridView view) {
        this.view = view;
    }

    public IGridView getView() {
        return view;
    }

    @Override
    public void tick(int x, int y) {
        if (wasConnected != grid.isActive()) {
            wasConnected = grid.isActive();

            view.sort();
        }

        if (isKeyDown(RSKeyBindings.CLEAR_GRID_CRAFTING_MATRIX)) {
            RS.NETWORK_HANDLER.sendToServer(new GridClearMessage());
        }

        tabs.update();
    }

    @Override
    public int getTopHeight() {
        return 19;
    }

    @Override
    public int getBottomHeight() {
        if (grid.getGridType() == GridType.CRAFTING) {
            return 156;
        } else if (grid.getGridType() == GridType.PATTERN) {
            return 169;
        } else {
            return 99;
        }
    }

    @Override
    public int getYPlayerInventory() {
        int yp = getTopHeight() + (getVisibleRows() * 18);

        if (grid.getGridType() == GridType.NORMAL || grid.getGridType() == GridType.FLUID) {
            yp += 16;
        } else if (grid.getGridType() == GridType.CRAFTING) {
            yp += 73;
        } else if (grid.getGridType() == GridType.PATTERN) {
            yp += 86;
        }

        return yp;
    }

    @Override
    public int getRows() {
        return Math.max(0, (int) Math.ceil((float) view.getStacks().size() / 9F));
    }

    @Override
    public int getCurrentOffset() {
        return scrollbar.getOffset();
    }

    @Override
    public String getSearchFieldText() {
        return searchField.getText();
    }

    @Override
    public int getVisibleRows() {
        switch (grid.getSize()) {
            case IGrid.SIZE_STRETCH:
                int screenSpaceAvailable = height - getTopHeight() - getBottomHeight();

                return Math.max(3, Math.min((screenSpaceAvailable / 18) - 3, RS.CLIENT_CONFIG.getGrid().getMaxRowsStretch()));
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

    private boolean isOverSlotWithStack() {
        return grid.isActive() && isOverSlot() && slotNumber < view.getStacks().size();
    }

    private boolean isOverSlot() {
        return slotNumber >= 0;
    }

    public boolean isOverSlotArea(double mouseX, double mouseY) {
        return RenderUtils.inBounds(7, 19, 162, 18 * getVisibleRows(), mouseX, mouseY);
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    private boolean isOverClear(double mouseX, double mouseY) {
        int y = getTopHeight() + (getVisibleRows() * 18) + 4;

        switch (grid.getGridType()) {
            case CRAFTING:
                return RenderUtils.inBounds(82, y, 7, 7, mouseX, mouseY);
            case PATTERN:
                if (((GridNetworkNode) grid).isProcessingPattern()) {
                    return RenderUtils.inBounds(154, y, 7, 7, mouseX, mouseY);
                }

                return RenderUtils.inBounds(82, y, 7, 7, mouseX, mouseY);
            default:
                return false;
        }
    }

    private boolean isOverCreatePattern(double mouseX, double mouseY) {
        return grid.getGridType() == GridType.PATTERN && RenderUtils.inBounds(172, getTopHeight() + (getVisibleRows() * 18) + 22, 16, 16, mouseX, mouseY) && ((GridNetworkNode) grid).canCreatePattern();
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        tabs.drawBackground(x, y - tabs.getHeight());

        if (grid instanceof IPortableGrid) {
            bindTexture(RS.ID, "gui/portable_grid.png");
        } else if (grid.getGridType() == GridType.CRAFTING) {
            bindTexture(RS.ID, "gui/crafting_grid.png");
        } else if (grid.getGridType() == GridType.PATTERN) {
            bindTexture(RS.ID, "gui/pattern_grid" + (((GridNetworkNode) grid).isProcessingPattern() ? "_processing" : "") + ".png");
        } else {
            bindTexture(RS.ID, "gui/grid.png");
        }

        int yy = y;

        blit(x, yy, 0, 0, xSize - 34, getTopHeight());

        // Filters and/or portable grid disk
        blit(x + xSize - 34 + 4, y, 197, 0, 30, grid instanceof IPortableGrid ? 114 : 82);

        int rows = getVisibleRows();

        for (int i = 0; i < rows; ++i) {
            yy += 18;

            blit(x, yy, 0, getTopHeight() + (i > 0 ? (i == rows - 1 ? 18 * 2 : 18) : 0), xSize - 34, 18);
        }

        yy += 18;

        blit(x, yy, 0, getTopHeight() + (18 * 3), xSize - 34, getBottomHeight());

        if (grid.getGridType() == GridType.PATTERN) {
            int ty = 0;

            if (isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
                ty = 1;
            }

            if (!((GridNetworkNode) grid).canCreatePattern()) {
                ty = 2;
            }

            blit(x + 172, y + getTopHeight() + (getVisibleRows() * 18) + 22, 240, ty * 16, 16, 16);
        }

        tabs.drawForeground(x, y - tabs.getHeight(), mouseX, mouseY, true);

        searchField.render(0, 0, 0);

        scrollbar.render();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        // Drawn in here for bug #1844 (https://github.com/raoulvdberge/refinedstorage/issues/1844)
        // Item tooltips can't be rendered in the foreground layer due to the X offset translation.
        if (isOverSlotWithStack()) {
            drawGridTooltip(view.getStacks().get(slotNumber), mouseX, mouseY);
        }
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, title.getFormattedText());
        renderString(7, getYPlayerInventory() - 12, I18n.format("container.inventory"));

        int x = 8;
        int y = 19;

        this.slotNumber = -1;

        int slot = scrollbar != null ? (scrollbar.getOffset() * 9) : 0;

        RenderSystem.setupGui3DDiffuseLighting();

        for (int i = 0; i < 9 * getVisibleRows(); ++i) {
            if (RenderUtils.inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isActive()) {
                this.slotNumber = slot;
            }

            if (slot < view.getStacks().size()) {
                view.getStacks().get(slot).draw(this, x, y);
            }

            if (RenderUtils.inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isActive()) {
                int color = grid.isActive() ? -2130706433 : 0xFF5B5B5B;

                RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                fillGradient(x, y, x + 16, y + 16, color, color);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableLighting();
                RenderSystem.enableDepthTest();
            }

            slot++;

            x += 18;

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            }
        }

        if (isOverClear(mouseX, mouseY)) {
            renderTooltip(mouseX, mouseY, I18n.format("misc.refinedstorage.clear"));
        }

        if (isOverCreatePattern(mouseX, mouseY)) {
            renderTooltip(mouseX, mouseY, I18n.format("gui.refinedstorage.grid.pattern_create"));
        }

        tabs.drawTooltip(font, mouseX, mouseY);
    }

    private void drawGridTooltip(IGridStack gridStack, int mouseX, int mouseY) {
        List<String> textLines = Lists.newArrayList(gridStack.getTooltip().split("\n"));
        List<String> smallTextLines = Lists.newArrayList();

        if (!gridStack.isCraftable()) {
            smallTextLines.add(I18n.format("misc.refinedstorage.total", gridStack.getFormattedFullQuantity()));
        }

        if (gridStack.getTrackerEntry() != null) {
            smallTextLines.add(TimeUtils.getAgo(gridStack.getTrackerEntry().getTime(), gridStack.getTrackerEntry().getName()));
        }

        ItemStack stack = gridStack instanceof ItemGridStack ? ((ItemGridStack) gridStack).getStack() : ItemStack.EMPTY;

        RenderUtils.drawTooltipWithSmallText(textLines, smallTextLines, RS.CLIENT_CONFIG.getGrid().getDetailedTooltip(), stack, mouseX, mouseY, xSize, ySize, font);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        if (tabs.mouseClicked()) {
            return true;
        }

        if (scrollbar.mouseClicked(mouseX, mouseY, clickedButton)) {
            return true;
        }

        if (RS.CLIENT_CONFIG.getGrid().getPreventSortingWhileShiftIsDown()) {
            doSort = !isOverSlotArea(mouseX - guiLeft, mouseY - guiTop) && !isOverCraftingOutputArea(mouseX - guiLeft, mouseY - guiTop);
        }

        boolean clickedClear = clickedButton == 0 && isOverClear(mouseX - guiLeft, mouseY - guiTop);
        boolean clickedCreatePattern = clickedButton == 0 && isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop);

        if (clickedCreatePattern) {
            minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            RS.NETWORK_HANDLER.sendToServer(new GridPatternCreateMessage(((GridNetworkNode) grid).getPos()));

            return true;
        } else if (grid.isActive()) {
            if (clickedClear) {
                minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                RS.NETWORK_HANDLER.sendToServer(new GridClearMessage());

                return true;
            }

            ItemStack held = container.getPlayer().inventory.getItemStack();

            if (isOverSlotArea(mouseX - guiLeft, mouseY - guiTop) && !held.isEmpty() && (clickedButton == 0 || clickedButton == 1)) {
                if (grid.getGridType() == GridType.FLUID) {
                    RS.NETWORK_HANDLER.sendToServer(new GridFluidInsertHeldMessage());
                } else {
                    RS.NETWORK_HANDLER.sendToServer(new GridItemInsertHeldMessage(clickedButton == 1));
                }

                return true;
            }

            if (isOverSlotWithStack()) {
                boolean isMiddleClickPulling = !held.isEmpty() && clickedButton == 2;
                boolean isPulling = held.isEmpty() || isMiddleClickPulling;

                IGridStack stack = view.getStacks().get(slotNumber);

                if (isPulling) {
                    if (view.canCraft() && stack.isCraftable()) {
                        minecraft.displayGuiScreen(new CraftingSettingsScreen(this, playerInventory.player, stack));
                    } else if (view.canCraft() && !stack.isCraftable() && stack.getOtherId() != null && hasShiftDown() && hasControlDown()) {
                        minecraft.displayGuiScreen(new CraftingSettingsScreen(this, playerInventory.player, view.get(stack.getOtherId())));
                    } else if (grid.getGridType() == GridType.FLUID && held.isEmpty()) {
                        RS.NETWORK_HANDLER.sendToServer(new GridFluidPullMessage(view.getStacks().get(slotNumber).getId(), hasShiftDown()));
                    } else if (grid.getGridType() != GridType.FLUID) {
                        int flags = 0;

                        if (clickedButton == 1) {
                            flags |= IItemGridHandler.EXTRACT_HALF;
                        }

                        if (hasShiftDown()) {
                            flags |= IItemGridHandler.EXTRACT_SHIFT;
                        }

                        if (clickedButton == 2) {
                            flags |= IItemGridHandler.EXTRACT_SINGLE;
                        }

                        RS.NETWORK_HANDLER.sendToServer(new GridItemPullMessage(stack.getId(), flags));
                    }
                }

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    private boolean isOverCraftingOutputArea(double mouseX, double mouseY) {
        if (grid.getGridType() != GridType.CRAFTING) {
            return false;
        }
        return RenderUtils.inBounds(130, getTopHeight() + getVisibleRows() * 18 + 18, 24, 24, mouseX, mouseY);
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

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (searchField.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            return true;
        }

        return super.charTyped(p_charTyped_1_, p_charTyped_2_);
    }

    @Override
    public boolean keyReleased(int key, int p_223281_2_, int p_223281_3_) {
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            view.sort();
        }
        return super.keyReleased(key, p_223281_2_, p_223281_3_);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeScreen();

            return true;
        }

        if (searchField.keyPressed(key, scanCode, modifiers) || searchField.func_212955_f()) {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    public SearchWidget getSearchField() {
        return searchField;
    }

    public void updateExactPattern(boolean checked) {
        if (exactPattern != null) {
            exactPattern.setIsChecked(checked);
        }
    }

    public void updateScrollbar() {
        scrollbar.setEnabled(getRows() > getVisibleRows());
        scrollbar.setMaxOffset(getRows() - getVisibleRows());
    }

    public boolean canSort() {
        return doSort || !hasShiftDown();
    }

    public static List<IGridSorter> getSorters() {
        List<IGridSorter> sorters = new LinkedList<>();
        sorters.add(getDefaultSorter());
        sorters.add(new QuantityGridSorter());
        sorters.add(new IdGridSorter());
        sorters.add(new LastModifiedGridSorter());
        sorters.add(new InventoryTweaksGridSorter());

        return sorters;
    }

    public static IGridSorter getDefaultSorter() {
        return new NameGridSorter();
    }
}
