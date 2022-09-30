package com.refinedmods.refinedstorage.screen.grid;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSKeyBindings;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.render.ElementDrawers;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.network.grid.*;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider;
import com.refinedmods.refinedstorage.screen.grid.sorting.*;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import com.refinedmods.refinedstorage.screen.grid.view.GridViewImpl;
import com.refinedmods.refinedstorage.screen.grid.view.IGridView;
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget;
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.screen.widget.SearchWidget;
import com.refinedmods.refinedstorage.screen.widget.TabListWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.portable.IPortableGrid;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGridBlockEntity;
import com.refinedmods.refinedstorage.util.RenderUtils;
import com.refinedmods.refinedstorage.util.TimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import yalter.mousetweaks.api.MouseTweaksDisableWheelTweak;

import java.util.LinkedList;
import java.util.List;

@MouseTweaksDisableWheelTweak
public class GridScreen extends BaseScreen<GridContainerMenu> implements IScreenInfoProvider {
    private static String searchQuery = "";
    private final IGrid grid;
    private final TabListWidget<GridContainerMenu> tabs;
    private final int patternScrollOffsetAbsoluteMax = GridNetworkNode.PROCESSING_MATRIX_SIZE / 3 - 3;
    private IGridView view;
    private SearchWidget searchField;
    private CheckboxWidget exactPattern;
    private CheckboxWidget processingPattern;
    private CheckboxWidget fluidCheckBox;
    private ScrollbarWidget scrollbar;
    private ScrollbarWidget patternScrollbar;
    private boolean wasConnected;
    private boolean doSort;
    private int slotNumber;
    private int patternScrollOffset;
    private int patternScrollOffsetMax;
    private boolean updatePatternOffset;

    public GridScreen(GridContainerMenu containerMenu, IGrid grid, Inventory inventory, Component title) {
        super(containerMenu, 227, 0, inventory, title);

        this.grid = grid;
        this.view = new GridViewImpl(this, getDefaultSorter(), getSorters());
        this.wasConnected = this.grid.isGridActive();
        this.tabs = new TabListWidget<>(this, new ElementDrawers<>(this), grid::getTabs, grid::getTotalTabPages, grid::getTabPage, grid::getTabSelected, IGrid.TABS_PER_PAGE);
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

        if (grid instanceof GridNetworkNode && grid.getGridType() == GridType.PATTERN) {
            GridNetworkNode node = (GridNetworkNode) grid;
            node.getProcessingMatrix().addListener((handler, slot, reading) -> updatePatternOffset = true);
            node.getProcessingMatrixFluids().addListener((handler, slot, reading) -> updatePatternOffset = true);
        }
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

    @Override
    protected void onPreInit() {
        super.onPreInit();
        this.doSort = true;
        this.imageHeight = getTopHeight() + getBottomHeight() + (getVisibleRows() * 18);
    }

    @Override
    public void onPostInit(int x, int y) {
        this.menu.initSlots();

        this.tabs.init(imageWidth - 32);

        this.scrollbar = new ScrollbarWidget(this, 174, getTopHeight(), 12, (getVisibleRows() * 18) - 2);

        if (grid instanceof GridNetworkNode || grid instanceof PortableGridBlockEntity) {
            addSideButton(new RedstoneModeSideButton(this, grid instanceof GridNetworkNode ? NetworkNodeBlockEntity.REDSTONE_MODE : PortableGridBlockEntity.REDSTONE_MODE));
        }

        int sx = x + 80 + 1;
        int sy = y + 6 + 1;

        if (searchField == null) {
            searchField = new SearchWidget(font, sx, sy, 88 - 6);
            searchField.setResponder(value -> {
                searchField.updateJei();

                getView().sort(); // Use getter since this view can be replaced.

                searchQuery = value;
            });
            searchField.setMode(grid.getSearchBoxMode());
            searchField.setValue(searchQuery);
        } else {
            searchField.x = sx;
            searchField.y = sy;
        }

        addRenderableWidget(searchField);

        if (grid.getViewType() != -1) {
            addSideButton(new GridViewTypeSideButton(this, grid));
        }

        addSideButton(new GridSortingDirectionSideButton(this, grid));
        addSideButton(new GridSortingTypeSideButton(this, grid));
        addSideButton(new GridSearchBoxModeSideButton(this));
        addSideButton(new GridSizeSideButton(this, grid::getSize, grid::onSizeChanged));

        if (grid.getGridType() == GridType.PATTERN) {
            patternScrollbar = new ScrollbarWidget(this, 160, getTopHeight() + getVisibleRows() * 18 + 4, 6, 18 * 3 - 2, true);

            patternScrollbar.setMaxOffset(patternScrollOffsetAbsoluteMax);
            patternScrollbar.setOffset(patternScrollOffset); // keep offset when changing between fluid and item view

            menu.updatePatternSlotPositions(patternScrollOffset);
            patternScrollbar.addListener((oldOffset, newOffset) -> {
                patternScrollOffset = newOffset;
                menu.updatePatternSlotPositions(newOffset);
            });

            processingPattern = addCheckBox(x + 7, y + getTopHeight() + (getVisibleRows() * 18) + 60, Component.translatable("misc.refinedstorage.processing"), GridBlockEntity.PROCESSING_PATTERN.getValue(), btn -> {
                // Rebuild the inventory slots before the slot change packet arrives.
                GridBlockEntity.PROCESSING_PATTERN.setValue(false, processingPattern.selected());
                ((GridNetworkNode) grid).clearMatrix(); // The server does this but let's do it earlier so the client doesn't notice.
                this.menu.initSlots();

                patternScrollOffset = 0; // reset offset when switching between crafting and processing
                BlockEntitySynchronizationManager.setParameter(GridBlockEntity.PROCESSING_PATTERN, processingPattern.selected());
            });

            if (!processingPattern.selected()) {
                exactPattern = addCheckBox(
                    processingPattern.x + processingPattern.getWidth() + 5,
                    y + getTopHeight() + (getVisibleRows() * 18) + 60,
                    Component.translatable("misc.refinedstorage.exact"),
                    GridBlockEntity.EXACT_PATTERN.getValue(),
                    btn -> BlockEntitySynchronizationManager.setParameter(GridBlockEntity.EXACT_PATTERN, exactPattern.selected())
                );
                patternScrollbar.setEnabled(false);
            } else {
                patternScrollbar.setEnabled(true);

                fluidCheckBox = addCheckBox(processingPattern.x + processingPattern.getWidth() + 5, y + getTopHeight() + (getVisibleRows() * 18) + 60, Component.translatable("misc.refinedstorage.fluidmode"), ((GridNetworkNode) grid).getType() == IType.FLUIDS, button -> {
                    BlockEntitySynchronizationManager.setParameter(GridBlockEntity.PROCESSING_TYPE, GridBlockEntity.PROCESSING_TYPE.getValue() == IType.ITEMS ? IType.FLUIDS : IType.ITEMS);
                });
            }
        }

        updateScrollbar();
    }

    public IGrid getGrid() {
        return grid;
    }

    public IGridView getView() {
        return view;
    }

    public void setView(IGridView view) {
        this.view = view;
    }

    @Override
    public void tick(int x, int y) {
        if (wasConnected != grid.isGridActive()) {
            wasConnected = grid.isGridActive();

            view.sort();
        }

        if (updatePatternOffset) {
            updatePatternOffsetMax();
            updatePatternOffset = false;
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
        return searchField.getValue();
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
        return grid.isGridActive() && isOverSlot() && slotNumber < view.getStacks().size();
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
                    return RenderUtils.inBounds(149, y, 7, 7, mouseX, mouseY);
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
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        tabs.drawBackground(poseStack, x, y - tabs.getHeight());

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

        blit(poseStack, x, yy, 0, 0, imageWidth - 34, getTopHeight());

        // Filters and/or portable grid disk
        blit(poseStack, x + imageWidth - 34 + 4, y, 197, 0, 30, grid instanceof IPortableGrid ? 114 : 82);

        int rows = getVisibleRows();

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

            blit(poseStack, x, yy, 0, yTextureStart, imageWidth - 34, 18);
        }

        yy += 18;

        blit(poseStack, x, yy, 0, getTopHeight() + (18 * 3), imageWidth - 34, getBottomHeight());

        if (grid.getGridType() == GridType.PATTERN) {
            int ty = 0;

            if (isOverCreatePattern(mouseX - leftPos, mouseY - topPos)) {
                ty = 1;
            }

            if (!((GridNetworkNode) grid).canCreatePattern()) {
                ty = 2;
            }

            blit(poseStack, x + 172, y + getTopHeight() + (getVisibleRows() * 18) + 22, 240, ty * 16, 16, 16);
            if (processingPattern.selected()) {
                updatePatternScrollbar();
                patternScrollbar.render(poseStack);
            }
        }

        tabs.drawForeground(poseStack, x, y - tabs.getHeight(), mouseX, mouseY, true);

        searchField.render(poseStack, 0, 0, 0);

        scrollbar.render(poseStack);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);

        // Drawn in here for bug #1844 (https://github.com/refinedmods/refinedstorage/issues/1844)
        // Item tooltips can't be rendered in the foreground layer due to the X offset translation.
        if (isOverSlotWithStack()) {
            drawGridTooltip(poseStack, view.getStacks().get(slotNumber), mouseX, mouseY);
        }
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY) {
        renderString(poseStack, 7, 7, title.getString());
        renderString(poseStack, 7, getYPlayerInventory() - 12, I18n.get("container.inventory"));

        int x = 8;
        int y = 19;

        this.slotNumber = -1;

        int slot = scrollbar != null ? (scrollbar.getOffset() * 9) : 0;

        Lighting.setupFor3DItems();

        for (int i = 0; i < 9 * getVisibleRows(); ++i) {
            if (RenderUtils.inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isGridActive()) {
                this.slotNumber = slot;
            }

            if (slot < view.getStacks().size()) {
                view.getStacks().get(slot).draw(poseStack, this, x, y);
            }

            if (RenderUtils.inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isGridActive()) {
                int color = grid.isGridActive() ? -2130706433 : 0xFF5B5B5B;

                poseStack.pushPose();
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                fillGradient(poseStack, x, y, x + 16, y + 16, color, color);
                RenderSystem.colorMask(true, true, true, true);
                poseStack.popPose();
            }

            slot++;

            x += 18;

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            }
        }

        if (isOverClear(mouseX, mouseY)) {
            renderTooltip(poseStack, mouseX, mouseY, I18n.get("misc.refinedstorage.clear"));
        }

        if (isOverCreatePattern(mouseX, mouseY)) {
            renderTooltip(poseStack, mouseX, mouseY, I18n.get("gui.refinedstorage.grid.pattern_create"));
        }

        tabs.drawTooltip(poseStack, font, mouseX, mouseY);
    }

    private void drawGridTooltip(PoseStack poseStack, IGridStack gridStack, int mouseX, int mouseY) {
        List<Component> textLines = gridStack.getTooltip(true);

        ItemStack stackContext = gridStack instanceof ItemGridStack ? ((ItemGridStack) gridStack).getStack() : ItemStack.EMPTY;

        if (RS.CLIENT_CONFIG.getGrid().getDetailedTooltip()) {
            Style detailedTextStyle = Style.EMPTY.applyFormat(ChatFormatting.GRAY).withItalic(true);

            if (!gridStack.isCraftable()) {
                textLines.add(Component.translatable("misc.refinedstorage.total", gridStack.getFormattedFullQuantity())
                    .withStyle(detailedTextStyle));
            }

            if (gridStack.getTrackerEntry() != null) {
                textLines.add(Component.translatable(TimeUtils.getAgo(gridStack.getTrackerEntry().getTime(), gridStack.getTrackerEntry().getName()))
                    .withStyle(detailedTextStyle));
            }
        }

        renderTooltip(poseStack, stackContext, mouseX, mouseY, textLines);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        if (tabs.mouseClicked()) {
            return true;
        }

        if (scrollbar.mouseClicked(mouseX, mouseY, clickedButton)) {
            return true;
        }
        if (grid.getGridType() == GridType.PATTERN && patternScrollbar.mouseClicked(mouseX, mouseY, clickedButton)) {
            return true;
        }
        if (RS.CLIENT_CONFIG.getGrid().getPreventSortingWhileShiftIsDown()) {
            doSort = !isOverSlotArea(mouseX - leftPos, mouseY - topPos) && !isOverCraftingOutputArea(mouseX - leftPos, mouseY - topPos);
        }

        boolean clickedClear = clickedButton == 0 && isOverClear(mouseX - leftPos, mouseY - topPos);
        boolean clickedCreatePattern = clickedButton == 0 && isOverCreatePattern(mouseX - leftPos, mouseY - topPos);

        if (clickedCreatePattern) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            RS.NETWORK_HANDLER.sendToServer(new GridPatternCreateMessage(((GridNetworkNode) grid).getPos()));

            return true;
        } else if (clickedClear) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            RS.NETWORK_HANDLER.sendToServer(new GridClearMessage());

            return true;
        } else if (grid.isGridActive()) {
            ItemStack held = menu.getCarried();

            if (isOverSlotArea(mouseX - leftPos, mouseY - topPos) && !held.isEmpty() && (clickedButton == 0 || clickedButton == 1)) {
                if (grid.getGridType() == GridType.FLUID) {
                    RS.NETWORK_HANDLER.sendToServer(new GridFluidInsertHeldMessage());
                } else {
                    RS.NETWORK_HANDLER.sendToServer(new GridItemInsertHeldMessage(clickedButton == 1));
                }

                return true;
            }

            if (isOverSlotWithStack()) {
                IGridStack stack = view.getStacks().get(slotNumber);

                if (held.isEmpty()) {
                    if (view.canCraft() && stack.isCraftable()) {
                        minecraft.setScreen(new CraftingSettingsScreen(this, inventory.player, stack));
                    } else if (view.canCraft() && !stack.isCraftable() && stack.getOtherId() != null && hasShiftDown() && hasControlDown()) {
                        minecraft.setScreen(new CraftingSettingsScreen(this, inventory.player, view.get(stack.getOtherId())));
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

        if (grid.getGridType() == GridType.PATTERN) {
            patternScrollbar.mouseMoved(mx, my);
        }
        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (grid.getGridType() == GridType.PATTERN && patternScrollbar.mouseReleased(mx, my, button)) {
            return true;
        }
        return scrollbar.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (hasShiftDown() || hasControlDown()) {
            if (RS.CLIENT_CONFIG.getGrid().getPreventSortingWhileShiftIsDown()) {
                doSort = !isOverSlotArea(x - leftPos, y - topPos) && !isOverCraftingOutputArea(x - leftPos, y - topPos);
            }
            if (grid.getGridType() != GridType.FLUID) {
                if (isOverInventory(x - leftPos, y - topPos) && hoveredSlot != null && hoveredSlot.hasItem() && getMenu().getDisabledSlotNumber() != hoveredSlot.getSlotIndex()) {
                    RS.NETWORK_HANDLER.sendToServer(new GridItemInventoryScrollMessage(hoveredSlot.getSlotIndex(), hasShiftDown(), delta > 0));
                } else if (isOverSlotArea(x - leftPos, y - topPos)) {
                    RS.NETWORK_HANDLER.sendToServer(new GridItemGridScrollMessage(isOverSlotWithStack() ? view.getStacks().get(slotNumber).getId() : null, hasShiftDown(), delta > 0));
                }
            }

            return super.mouseScrolled(x, y, delta);
        }

        if (grid.getGridType() == GridType.PATTERN && isOverPatternArea(x - leftPos, y - topPos) && patternScrollbar.mouseScrolled(x, y, delta)) {
            return true;
        }

        return this.scrollbar.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta);
    }

    private boolean isOverInventory(double x, double y) {
        return RenderUtils.inBounds(8, getYPlayerInventory(), 9 * 18 - 2, 4 * 18 + 2, x, y);
    }

    private boolean isOverPatternArea(double x, double y) {
        return RenderUtils.inBounds(8, getTopHeight() + getVisibleRows() * 18, 152, 54, x, y);
    }

    @Override
    public boolean charTyped(char unknown1, int unknown2) {
        if (searchField.charTyped(unknown1, unknown2)) {
            return true;
        }

        return super.charTyped(unknown1, unknown2);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT ||
            keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT ||
            keyCode == GLFW.GLFW_KEY_LEFT_CONTROL ||
            keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            view.sort();
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (searchField.keyPressed(key, scanCode, modifiers) || searchField.canConsumeInput()) {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void removed() {
        super.removed();

        if (!RS.CLIENT_CONFIG.getGrid().getRememberSearchQuery()) {
            searchQuery = "";
        }
    }

    public SearchWidget getSearchField() {
        return searchField;
    }

    public void updateExactPattern(boolean checked) {
        if (exactPattern != null) {
            exactPattern.setChecked(checked);
        }
    }

    //check processing matrix and update maximum offset depending on contents
    private void updatePatternOffsetMax() {
        int filledInputSlots = 0;
        int filledOutputSlots = 0;
        int lastFilledInputSlot = 0;
        int lastFilledOutputSlot = 0;

        for (int i = 0; i < GridNetworkNode.PROCESSING_MATRIX_SIZE * 2; i++) {
            if (!isMatrixSlotEmpty(i)) {
                if (i > GridNetworkNode.PROCESSING_MATRIX_SIZE - 1) {
                    filledOutputSlots++;
                    lastFilledOutputSlot = i - GridNetworkNode.PROCESSING_MATRIX_SIZE - 1;
                } else {
                    filledInputSlots++;
                    lastFilledInputSlot = i;
                }
            }
        }

        int maxFilledSlots = Math.max(filledInputSlots, filledOutputSlots);
        int maxLastFilledSlot = Math.max(lastFilledInputSlot, lastFilledOutputSlot);

        // offset calculation to show next row if 4 of 9 slots are filled.
        int filledSlotOffset = Math.floorDiv(maxFilledSlots - 1, 3);
        // offset to show the last item in the matrix
        int lastSlotOffset = Math.floorDiv(maxLastFilledSlot, 3) - 2;

        patternScrollOffsetMax = Math.max(filledSlotOffset, lastSlotOffset);
    }

    private boolean isMatrixSlotEmpty(int slotNumber) {
        return ((GridNetworkNode) grid).getProcessingMatrix().getStackInSlot(slotNumber).isEmpty()
            && ((GridNetworkNode) grid).getProcessingMatrixFluids().getFluid(slotNumber).isEmpty();
    }

    public void updatePatternScrollbar() {
        patternScrollbar.setEnabled(processingPattern.selected() && patternScrollOffsetMax > 0);
        int oldOffset = patternScrollbar.getOffset();
        patternScrollbar.setMaxOffset(Math.min(patternScrollOffsetMax, patternScrollOffsetAbsoluteMax));

        if (oldOffset != patternScrollbar.getOffset()) {
            menu.updatePatternSlotPositions(patternScrollbar.getOffset());
        }
    }

    public void updateScrollbar() {
        scrollbar.setEnabled(getRows() > getVisibleRows());
        scrollbar.setMaxOffset(getRows() - getVisibleRows());
    }

    public boolean canSort() {
        return doSort || (!hasShiftDown() && !hasControlDown());
    }
}
