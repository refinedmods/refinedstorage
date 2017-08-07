package com.raoulvdberge.refinedstorage.gui.grid;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSKeyBindings;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.Scrollbar;
import com.raoulvdberge.refinedstorage.gui.grid.filtering.GridFilterParser;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.GridSorting;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.GridSortingID;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.GridSortingInventoryTweaks;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.GridSortingName;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.GridSortingQuantity;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.gui.sidebutton.*;
import com.raoulvdberge.refinedstorage.integration.jei.IntegrationJEI;
import com.raoulvdberge.refinedstorage.integration.jei.RSJEIPlugin;
import com.raoulvdberge.refinedstorage.network.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.TilePortableGrid;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class GuiGrid extends GuiBase implements IGridDisplay {
    private static final GridSorting SORTING_QUANTITY = new GridSortingQuantity();
    private static final GridSorting SORTING_NAME = new GridSortingName();
    private static final GridSorting SORTING_ID = new GridSortingID();
    private static final GridSorting SORTING_INVENTORYTWEAKS = new GridSortingInventoryTweaks();

    private static final List<String> SEARCH_HISTORY = new ArrayList<>();

    public static final ListMultimap<Item, GridStackItem> ITEMS = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
    public static final ListMultimap<Fluid, GridStackFluid> FLUIDS = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    public static List<IGridStack> STACKS = new ArrayList<>();
    public static boolean CAN_CRAFT;

    private static boolean markedForSorting;

    private boolean wasConnected;

    private GuiTextField searchField;

    private GuiCheckBox oredictPattern;
    private GuiCheckBox processingPattern;
    private GuiCheckBox blockingPattern;

    private IGrid grid;

    private boolean hadTabs = false;
    private int tabHovering = -1;

    private int slotNumber;

    private int searchHistory = -1;

    private Deque<Integer> konami = new ArrayDeque<>(Arrays.asList(
        Keyboard.KEY_UP,
        Keyboard.KEY_UP,
        Keyboard.KEY_DOWN,
        Keyboard.KEY_DOWN,
        Keyboard.KEY_LEFT,
        Keyboard.KEY_RIGHT,
        Keyboard.KEY_LEFT,
        Keyboard.KEY_RIGHT,
        Keyboard.KEY_B,
        Keyboard.KEY_A
    ));

    private int[] konamiOffsetsX;
    private int[] konamiOffsetsY;

    public static void markForSorting() {
        markedForSorting = true;
    }

    public GuiGrid(ContainerGrid container, IGrid grid) {
        super(container, grid.getType() == GridType.FLUID ? 193 : 227, 0);

        this.grid = grid;
        this.wasConnected = this.grid.isActive();
    }

    @Override
    protected void calcHeight() {
        this.ySize = getHeader() + getFooter() + (getVisibleRows() * 18);

        if (hadTabs) {
            this.ySize += ContainerGrid.TAB_HEIGHT;
        }

        this.screenHeight = ySize;
    }

    @Override
    public void init(int x, int y) {
        ((ContainerGrid) this.inventorySlots).initSlots();

        this.scrollbar = new Scrollbar(174, getTabDelta() + getHeader(), 12, (getVisibleRows() * 18) - 2);

        if (grid instanceof NetworkNodeGrid || grid instanceof TilePortableGrid) {
            addSideButton(new SideButtonRedstoneMode(this, grid instanceof NetworkNodeGrid ? TileGrid.REDSTONE_MODE : TilePortableGrid.REDSTONE_MODE));
        }

        this.konamiOffsetsX = new int[9 * getVisibleRows()];
        this.konamiOffsetsY = new int[9 * getVisibleRows()];

        int sx = x + 80 + 1;
        int sy = y + 6 + 1 + getTabDelta();

        if (searchField == null) {
            searchField = new GuiTextField(0, fontRenderer, sx, sy, 88 - 6, fontRenderer.FONT_HEIGHT);
            searchField.setEnableBackgroundDrawing(false);
            searchField.setVisible(true);
            searchField.setTextColor(16777215);

            updateSearchFieldFocus(grid.getSearchBoxMode());
        } else {
            searchField.x = sx;
            searchField.y = sy;
        }

        if (grid.getType() == GridType.PATTERN) {
            processingPattern = addCheckBox(x + 7, y + getTabDelta() + getHeader() + (getVisibleRows() * 18) + 60, t("misc.refinedstorage:processing"), TileGrid.PROCESSING_PATTERN.getValue());
            oredictPattern = addCheckBox(processingPattern.x + processingPattern.width + 5, y + getTabDelta() + getHeader() + (getVisibleRows() * 18) + 60, t("misc.refinedstorage:oredict"), TileGrid.OREDICT_PATTERN.getValue());

            if (((NetworkNodeGrid) grid).isProcessingPattern()) {
                blockingPattern = addCheckBox(oredictPattern.x + oredictPattern.width + 5, y + getTabDelta() + getHeader() + (getVisibleRows() * 18) + 60, t("misc.refinedstorage:blocking"), TileGrid.BLOCKING_PATTERN.getValue());
            }
        }

        if (grid.getType() != GridType.FLUID && grid.getViewType() != -1) {
            addSideButton(new SideButtonGridViewType(this, grid));
        }

        addSideButton(new SideButtonGridSortingDirection(this, grid));
        addSideButton(new SideButtonGridSortingType(this, grid));
        addSideButton(new SideButtonGridSearchBoxMode(this));
        addSideButton(new SideButtonGridSize(this, grid));

        sortItems();
    }

    @Override
    protected int getSideButtonYStart() {
        return super.getSideButtonYStart() + (!grid.getTabs().isEmpty() ? ContainerGrid.TAB_HEIGHT - 3 : 0);
    }

    public IGrid getGrid() {
        return grid;
    }

    private void sortItems() {
        List<IGridStack> stacks = new ArrayList<>();

        if (grid.isActive()) {
            stacks.addAll(grid.getType() == GridType.FLUID ? FLUIDS.values() : ITEMS.values());

            List<Predicate<IGridStack>> filters = GridFilterParser.getFilters(
                grid,
                searchField != null ? searchField.getText() : "",
                (grid.getTabSelected() >= 0 && grid.getTabSelected() < grid.getTabs().size()) ? grid.getTabs().get(grid.getTabSelected()).getFilters() : grid.getFilters()
            );

            Iterator<IGridStack> t = stacks.iterator();

            while (t.hasNext()) {
                IGridStack stack = t.next();

                for (Predicate<IGridStack> filter : filters) {
                    if (!filter.test(stack)) {
                        t.remove();

                        break;
                    }
                }
            }

            SORTING_NAME.setSortingDirection(grid.getSortingDirection());
            SORTING_QUANTITY.setSortingDirection(grid.getSortingDirection());
            SORTING_ID.setSortingDirection(grid.getSortingDirection());
            SORTING_INVENTORYTWEAKS.setSortingDirection(grid.getSortingDirection());

            stacks.sort(SORTING_NAME);

            if (grid.getSortingType() == IGrid.SORTING_TYPE_QUANTITY) {
                stacks.sort(SORTING_QUANTITY);
            } else if (grid.getSortingType() == IGrid.SORTING_TYPE_ID) {
                stacks.sort(SORTING_ID);
            } else if (grid.getSortingType() == IGrid.SORTING_TYPE_INVENTORYTWEAKS) {
                stacks.sort(SORTING_INVENTORYTWEAKS);
            }
        }

        STACKS = stacks;

        if (scrollbar != null) {
            scrollbar.setEnabled(getRows() > getVisibleRows());
            scrollbar.setMaxOffset(getRows() - getVisibleRows());
        }
    }

    @Override
    public void update(int x, int y) {
        if (konami.isEmpty()) {
            for (int i = 0; i < 9 * getVisibleRows(); ++i) {
                konamiOffsetsX[i] += (ThreadLocalRandom.current().nextBoolean() ? 1 : -1) * ThreadLocalRandom.current().nextInt(5);
                konamiOffsetsY[i] += (ThreadLocalRandom.current().nextBoolean() ? 1 : -1) * ThreadLocalRandom.current().nextInt(5);
            }
        }

        if (wasConnected != grid.isActive()) {
            wasConnected = grid.isActive();

            markForSorting();
        }

        if (markedForSorting) {
            markedForSorting = false;

            sortItems();
        }

        boolean hasTabs = !getGrid().getTabs().isEmpty();

        if (hadTabs != hasTabs) {
            hadTabs = hasTabs;

            initGui();
        }
    }

    @Override
    public int getHeader() {
        return 19;
    }

    @Override
    public int getFooter() {
        if (grid.getType() == GridType.CRAFTING) {
            return 156;
        } else if (grid.getType() == GridType.PATTERN) {
            return 169;
        } else {
            return 99;
        }
    }

    @Override
    public int getYPlayerInventory() {
        int yp = getTabDelta() + getHeader() + (getVisibleRows() * 18);

        if (grid.getType() == GridType.NORMAL || grid.getType() == GridType.FLUID) {
            yp += 16;
        } else if (grid.getType() == GridType.CRAFTING) {
            yp += 73;
        } else if (grid.getType() == GridType.PATTERN) {
            yp += 86;
        }

        return yp;
    }

    @Override
    public int getRows() {
        return Math.max(0, (int) Math.ceil((float) STACKS.size() / 9F));
    }

    @Override
    public int getVisibleRows() {
        switch (grid.getSize()) {
            case IGrid.SIZE_STRETCH:
                int screenSpaceAvailable = height - getHeader() - getFooter() - (hadTabs ? ContainerGrid.TAB_HEIGHT : 0);

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

    private boolean isOverSlotWithStack() {
        return grid.isActive() && isOverSlot() && slotNumber < STACKS.size();
    }

    private boolean isOverSlot() {
        return slotNumber >= 0;
    }

    public boolean isOverSlotArea(int mouseX, int mouseY) {
        return inBounds(7, 19 + getTabDelta(), 162, 18 * getVisibleRows(), mouseX, mouseY);
    }

    private boolean isOverClear(int mouseX, int mouseY) {
        int y = getTabDelta() + getHeader() + (getVisibleRows() * 18) + 4;

        switch (grid.getType()) {
            case CRAFTING:
                return inBounds(82, y, 7, 7, mouseX, mouseY);
            case PATTERN:
                if (((NetworkNodeGrid) grid).isProcessingPattern()) {
                    return inBounds(154, y, 7, 7, mouseX, mouseY);
                }

                return inBounds(82, y, 7, 7, mouseX, mouseY);
            default:
                return false;
        }
    }

    private boolean isOverCreatePattern(int mouseX, int mouseY) {
        return grid.getType() == GridType.PATTERN && inBounds(172, getTabDelta() + getHeader() + (getVisibleRows() * 18) + 22, 16, 16, mouseX, mouseY) && ((NetworkNodeGrid) grid).canCreatePattern();
    }

    private int getTabDelta() {
        return !grid.getTabs().isEmpty() ? ContainerGrid.TAB_HEIGHT - 4 : 0;
    }

    private void drawTab(IGridTab tab, boolean foregroundLayer, int x, int y, int mouseX, int mouseY) {
        int i = grid.getTabs().indexOf(tab);
        boolean selected = i == grid.getTabSelected();

        if ((foregroundLayer && !selected) || (!foregroundLayer && selected)) {
            return;
        }

        int tx = x + ((ContainerGrid.TAB_WIDTH + 1) * i);
        int ty = y;

        bindTexture("icons.png");

        if (!selected) {
            ty += 3;
        }

        int uvx;
        int uvy = 225;
        int tbw = ContainerGrid.TAB_WIDTH;
        int otx = tx;

        if (selected) {
            uvx = 227;

            if (i > 0) {
                uvx = 226;
                uvy = 194;
                tbw++;
                tx--;
            }
        } else {
            uvx = 199;
        }

        drawTexture(tx, ty, uvx, uvy, tbw, ContainerGrid.TAB_HEIGHT);

        RenderHelper.enableGUIStandardItemLighting();

        drawItem(otx + 6, ty + 8 - (!selected ? 2 : 0), tab.getIcon());

        if (inBounds(tx, ty, ContainerGrid.TAB_WIDTH, ContainerGrid.TAB_HEIGHT, mouseX, mouseY)) {
            tabHovering = i;
        }
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        tabHovering = -1;

        for (IGridTab tab : grid.getTabs()) {
            drawTab(tab, false, x, y, mouseX, mouseY);
        }

        if (grid.getType() == GridType.CRAFTING) {
            bindTexture("gui/crafting_grid.png");
        } else if (grid.getType() == GridType.PATTERN) {
            bindTexture("gui/pattern_grid" + (((NetworkNodeGrid) grid).isProcessingPattern() ? "_processing" : "") + ".png");
        } else if (grid instanceof IPortableGrid) {
            bindTexture("gui/portable_grid.png");
        } else {
            bindTexture("gui/grid.png");
        }

        int yy = y + getTabDelta();

        drawTexture(x, yy, 0, 0, screenWidth - (grid.getType() != GridType.FLUID ? 34 : 0), getHeader());

        if (grid.getType() != GridType.FLUID) {
            drawTexture(x + screenWidth - 34 + 4, y + getTabDelta(), 197, 0, 30, grid instanceof IPortableGrid ? 114 : 82);
        }

        int rows = getVisibleRows();

        for (int i = 0; i < rows; ++i) {
            yy += 18;

            drawTexture(x, yy, 0, getHeader() + (i > 0 ? (i == rows - 1 ? 18 * 2 : 18) : 0), screenWidth - (grid.getType() != GridType.FLUID ? 34 : 0), 18);
        }

        yy += 18;

        drawTexture(x, yy, 0, getHeader() + (18 * 3), screenWidth - (grid.getType() != GridType.FLUID ? 34 : 0), getFooter());

        if (grid.getType() == GridType.PATTERN) {
            int ty = 0;

            if (isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
                ty = 1;
            }

            if (!((NetworkNodeGrid) grid).canCreatePattern()) {
                ty = 2;
            }

            drawTexture(x + 172, y + getTabDelta() + getHeader() + (getVisibleRows() * 18) + 22, 240, ty * 16, 16, 16);
        }

        for (IGridTab tab : grid.getTabs()) {
            drawTab(tab, true, x, y, mouseX, mouseY);
        }

        if (searchField != null) {
            searchField.drawTextBox();
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7 + getTabDelta(), t(grid.getGuiTitle()));
        drawString(7, getYPlayerInventory() - 12, t("container.inventory"));

        int x = 8;
        int y = 19 + getTabDelta();

        this.slotNumber = -1;

        int slot = scrollbar != null ? (scrollbar.getOffset() * 9) : 0;

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 9 * getVisibleRows(); ++i) {
            int xx = x + (konami.isEmpty() ? konamiOffsetsX[i] : 0);
            int yy = y + (konami.isEmpty() ? konamiOffsetsY[i] : 0);

            if (inBounds(xx, yy, 16, 16, mouseX, mouseY) || !grid.isActive()) {
                this.slotNumber = slot;
            }

            if (slot < STACKS.size()) {
                STACKS.get(slot).draw(this, xx, yy, GuiScreen.isShiftKeyDown() && slotNumber == slot);
            }

            if (inBounds(xx, yy, 16, 16, mouseX, mouseY) || !grid.isActive()) {
                int color = grid.isActive() ? -2130706433 : 0xFF5B5B5B;

                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                zLevel = 190;
                GlStateManager.colorMask(true, true, true, false);
                drawGradientRect(xx, yy, xx + 16, yy + 16, color, color);
                zLevel = 0;
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

            slot++;

            x += 18;

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            }
        }

        if (isOverSlotWithStack()) {
            IGridStack stack = STACKS.get(slotNumber);

            drawTooltip(stack instanceof GridStackItem ? ((GridStackItem) stack).getStack() : ItemStack.EMPTY, mouseX, mouseY, stack.getTooltip());
        }

        if (isOverClear(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:clear"));
        }

        if (isOverCreatePattern(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("gui.refinedstorage:grid.pattern_create"));
        }

        if (tabHovering >= 0 && tabHovering < grid.getTabs().size() && !grid.getTabs().get(tabHovering).getName().equalsIgnoreCase("")) {
            drawTooltip(mouseX, mouseY, grid.getTabs().get(tabHovering).getName());
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == oredictPattern) {
            TileDataManager.setParameter(TileGrid.OREDICT_PATTERN, oredictPattern.isChecked());
        } else if (button == blockingPattern) {
            TileDataManager.setParameter(TileGrid.BLOCKING_PATTERN, blockingPattern.isChecked());
        } else if (button == processingPattern) {
            TileDataManager.setParameter(TileGrid.PROCESSING_PATTERN, processingPattern.isChecked());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedButton);

        boolean wasSearchFieldFocused = searchField.isFocused();

        searchField.mouseClicked(mouseX, mouseY, clickedButton);

        if (tabHovering >= 0 && tabHovering < grid.getTabs().size()) {
            grid.onTabSelectionChanged(tabHovering);
        }

        if (clickedButton == 1 && inBounds(79, 5 + getTabDelta(), 90, 12, mouseX - guiLeft, mouseY - guiTop)) {
            searchField.setText("");
            searchField.setFocused(true);

            sortItems();

            updateJEI();
        } else if (wasSearchFieldFocused != searchField.isFocused()) {
            saveHistory();
        }

        boolean clickedClear = clickedButton == 0 && isOverClear(mouseX - guiLeft, mouseY - guiTop);
        boolean clickedCreatePattern = clickedButton == 0 && isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop);

        if (clickedCreatePattern) {
            BlockPos gridPos = ((NetworkNodeGrid) grid).getPos();

            RS.INSTANCE.network.sendToServer(new MessageGridPatternCreate(gridPos.getX(), gridPos.getY(), gridPos.getZ()));
        } else if (grid.isActive()) {
            if (clickedClear) {
                RS.INSTANCE.network.sendToServer(new MessageGridClear());
            }

            ItemStack held = ((ContainerGrid) this.inventorySlots).getPlayer().inventory.getItemStack();

            if (isOverSlotArea(mouseX - guiLeft, mouseY - guiTop) && !held.isEmpty() && (clickedButton == 0 || clickedButton == 1)) {
                RS.INSTANCE.network.sendToServer(grid.getType() == GridType.FLUID ? new MessageGridFluidInsertHeld() : new MessageGridItemInsertHeld(clickedButton == 1));
            }

            if (isOverSlotWithStack()) {
                if (grid.getType() != GridType.FLUID && (held.isEmpty() || (!held.isEmpty() && clickedButton == 2))) {
                    GridStackItem stack = (GridStackItem) STACKS.get(slotNumber);

                    if (stack.isCraftable() && (stack.doesDisplayCraftText() || (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown())) && CAN_CRAFT) {
                        FMLCommonHandler.instance().showGuiScreen(new GuiCraftingStart(this, ((ContainerGrid) this.inventorySlots).getPlayer(), stack));
                    } else {
                        int flags = 0;

                        if (clickedButton == 1) {
                            flags |= IItemGridHandler.EXTRACT_HALF;
                        }

                        if (GuiScreen.isShiftKeyDown()) {
                            flags |= IItemGridHandler.EXTRACT_SHIFT;
                        }

                        if (clickedButton == 2) {
                            flags |= IItemGridHandler.EXTRACT_SINGLE;
                        }

                        RS.INSTANCE.network.sendToServer(new MessageGridItemPull(stack.getHash(), flags));
                    }
                } else if (grid.getType() == GridType.FLUID && held.isEmpty()) {
                    RS.INSTANCE.network.sendToServer(new MessageGridFluidPull(STACKS.get(slotNumber).getHash(), GuiScreen.isShiftKeyDown()));
                }
            }
        }

        if (clickedClear || clickedCreatePattern) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!konami.isEmpty() && konami.peek() == keyCode) {
            konami.pop();
        }

        if (checkHotbarKeys(keyCode)) {
            // NO OP
        } else if (searchField.textboxKeyTyped(character, keyCode)) {
            updateJEI();

            sortItems();
            keyHandled = true;
        } else if (searchField.isFocused() && (keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_RETURN)) {
            if (keyCode == Keyboard.KEY_UP) {
                updateSearchHistory(-1);
            } else if (keyCode == Keyboard.KEY_DOWN) {
                updateSearchHistory(1);
            } else {
                saveHistory();

                if (grid.getSearchBoxMode() == IGrid.SEARCH_BOX_MODE_NORMAL || grid.getSearchBoxMode() == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED) {
                    searchField.setFocused(false);
                }
            }
            keyHandled = true;
        } else if (keyCode == RSKeyBindings.FOCUS_SEARCH_BAR.getKeyCode() && (grid.getSearchBoxMode() == IGrid.SEARCH_BOX_MODE_NORMAL || grid.getSearchBoxMode() == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED)) {
            searchField.setFocused(!searchField.isFocused());

            saveHistory();
            keyHandled = true;
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    private void updateSearchHistory(int delta) {
        if (searchHistory == -1) {
            searchHistory = SEARCH_HISTORY.size();
        }

        searchHistory += delta;

        if (searchHistory < 0) {
            searchHistory = 0;
        } else if (searchHistory > SEARCH_HISTORY.size() - 1) {
            searchHistory = SEARCH_HISTORY.size() - 1;

            if (delta == 1) {
                searchField.setText("");

                sortItems();

                updateJEI();

                return;
            }
        }

        searchField.setText(SEARCH_HISTORY.get(searchHistory));

        sortItems();

        updateJEI();
    }

    private void saveHistory() {
        if (!SEARCH_HISTORY.isEmpty() && SEARCH_HISTORY.get(SEARCH_HISTORY.size() - 1).equals(searchField.getText())) {
            return;
        }

        if (!searchField.getText().trim().isEmpty()) {
            SEARCH_HISTORY.add(searchField.getText());
        }
    }

    private void updateJEI() {
        if (IntegrationJEI.isLoaded() && (grid.getSearchBoxMode() == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED || grid.getSearchBoxMode() == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED)) {
            RSJEIPlugin.INSTANCE.getRuntime().getIngredientFilter().setFilterText(searchField.getText());
        }
    }

    public void updateSearchFieldFocus(int mode) {
        if (searchField != null) {
            searchField.setCanLoseFocus(!IGrid.isSearchBoxModeWithAutoselection(mode));
            searchField.setFocused(IGrid.isSearchBoxModeWithAutoselection(mode));
        }
    }

    public GuiTextField getSearchField() {
        return searchField;
    }

    public void updateOredictPattern(boolean checked) {
        if (oredictPattern != null) {
            oredictPattern.setIsChecked(checked);
        }
    }

    public void updateBlockingPattern(boolean checked) {
        if (blockingPattern != null) {
            blockingPattern.setIsChecked(checked);
        }
    }
}
