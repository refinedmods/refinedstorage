package com.raoulvdberge.refinedstorage.gui.grid;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.Scrollbar;
import com.raoulvdberge.refinedstorage.gui.grid.filtering.GridFilterParser;
import com.raoulvdberge.refinedstorage.gui.grid.filtering.IGridFilter;
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
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
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

public class GuiGrid extends GuiBase implements IGridDisplay {
    private static final GridSortingQuantity SORTING_QUANTITY = new GridSortingQuantity();
    private static final GridSortingName SORTING_NAME = new GridSortingName();

    public static final ListMultimap<Item, GridStackItem> ITEMS = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
    public static final ListMultimap<Fluid, GridStackFluid> FLUIDS = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    public static List<IGridStack> STACKS = new ArrayList<>();

    private static boolean markedForSorting;

    private boolean wasConnected;

    private GuiTextField searchField;
    private GuiCheckBox oredictPattern;

    private IGrid grid;

    private boolean hadTabs = false;
    private int tabHovering = -1;

    private int slotNumber;

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
        super(container, grid.getType() == EnumGridType.FLUID ? 193 : 227, 0);

        this.grid = grid;
        this.wasConnected = this.grid.isActive();

        this.konamiOffsetsX = new int[9 * getVisibleRows()];
        this.konamiOffsetsY = new int[9 * getVisibleRows()];
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

        if (grid.getRedstoneModeConfig() != null) {
            addSideButton(new SideButtonRedstoneMode(this, grid.getRedstoneModeConfig()));
        }

        int sx = x + 80 + 1;
        int sy = y + 6 + 1 + getTabDelta();

        if (searchField == null) {
            searchField = new GuiTextField(0, fontRendererObj, sx, sy, 88 - 6, fontRendererObj.FONT_HEIGHT);
            searchField.setEnableBackgroundDrawing(false);
            searchField.setVisible(true);
            searchField.setTextColor(16777215);

            updateSearchFieldFocus(grid.getSearchBoxMode());
        } else {
            searchField.xPosition = sx;
            searchField.yPosition = sy;
        }

        if (grid.getType() == EnumGridType.PATTERN) {
            oredictPattern = addCheckBox(x + 64, getTabDelta() + getHeader() + (getVisibleRows() * 18) + 78, t("misc.refinedstorage:oredict"), TileGrid.OREDICT_PATTERN.getValue());
        }

        if (grid.getType() != EnumGridType.FLUID) {
            addSideButton(new SideButtonGridViewType(this, grid));
        }

        addSideButton(new SideButtonGridSortingDirection(this, grid));
        addSideButton(new SideButtonGridSortingType(this, grid));
        addSideButton(new SideButtonGridSearchBoxMode(this));

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
            stacks.addAll(grid.getType() == EnumGridType.FLUID ? FLUIDS.values() : ITEMS.values());

            List<IGridFilter> filters = GridFilterParser.getFilters(
                grid,
                searchField.getText(),
                (grid.getTabSelected() >= 0 && grid.getTabSelected() < grid.getTabs().size()) ? grid.getTabs().get(grid.getTabSelected()).getFilters() : grid.getFilteredItems()
            );

            Iterator<IGridStack> t = stacks.iterator();

            while (t.hasNext()) {
                IGridStack stack = t.next();

                for (IGridFilter filter : filters) {
                    if (!filter.accepts(stack)) {
                        t.remove();

                        break;
                    }
                }
            }

            SORTING_NAME.setSortingDirection(grid.getSortingDirection());
            SORTING_QUANTITY.setSortingDirection(grid.getSortingDirection());

            Collections.sort(stacks, SORTING_NAME);

            if (grid.getSortingType() == TileGrid.SORTING_TYPE_QUANTITY) {
                Collections.sort(stacks, SORTING_QUANTITY);
            }
        }

        STACKS = stacks;

        scrollbar.setEnabled(getRows() > getVisibleRows());
        scrollbar.setMaxOffset(getRows() - getVisibleRows());
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
        return (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 156 : 99;
    }

    @Override
    public int getYPlayerInventory() {
        int yp = getTabDelta() + getHeader() + (getVisibleRows() * 18);

        if (grid.getType() == EnumGridType.NORMAL || grid.getType() == EnumGridType.FLUID) {
            yp += 16;
        } else if (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) {
            yp += 73;
        }

        return yp;
    }

    @Override
    public int getRows() {
        return Math.max(0, (int) Math.ceil((float) STACKS.size() / 9F));
    }

    @Override
    public int getVisibleRows() {
        int screenSpaceAvailable = height - getHeader() - getFooter() - (hadTabs ? ContainerGrid.TAB_HEIGHT : 0);

        return Math.max(3, (screenSpaceAvailable / 18) - 3);
    }

    private boolean isOverSlotWithItem() {
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
                return inBounds(64, y, 7, 7, mouseX, mouseY);
            default:
                return false;
        }
    }

    private boolean isOverCreatePattern(int mouseX, int mouseY) {
        return grid.getType() == EnumGridType.PATTERN && inBounds(152, getTabDelta() + getHeader() + (getVisibleRows() * 18) + 22, 16, 16, mouseX, mouseY) && ((TileGrid) grid).canCreatePattern();
    }

    private int getTabDelta() {
        return !grid.getTabs().isEmpty() ? ContainerGrid.TAB_HEIGHT - 4 : 0;
    }

    private void renderTab(GridTab tab, boolean foregroundLayer, int x, int y, int mouseX, int mouseY) {
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

        int uvx = 0;
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

        for (GridTab tab : grid.getTabs()) {
            renderTab(tab, false, x, y, mouseX, mouseY);
        }

        if (grid.getType() == EnumGridType.CRAFTING) {
            bindTexture("gui/crafting_grid.png");
        } else if (grid.getType() == EnumGridType.PATTERN) {
            bindTexture("gui/pattern_grid.png");
        } else {
            bindTexture("gui/grid.png");
        }

        int yy = y + getTabDelta();

        drawTexture(x, yy, 0, 0, screenWidth, getHeader());
        int r = getVisibleRows();

        for (int i = 0; i < r; ++i) {
            yy += 18;

            drawTexture(x, yy, 0, getHeader() + (i > 0 ? (i == r - 1 ? 18 * 2 : 18) : 0), screenWidth, 18);
        }

        yy += 18;

        drawTexture(x, yy, 0, getHeader() + (18 * 3), screenWidth, getFooter());

        for (GridTab tab : grid.getTabs()) {
            renderTab(tab, true, x, y, mouseX, mouseY);
        }

        if (grid.getType() == EnumGridType.PATTERN) {
            int ty = 0;

            if (isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
                ty = 1;
            }

            if (!((TileGrid) grid).canCreatePattern()) {
                ty = 2;
            }

            drawTexture(x + 152, y + getTabDelta() + getHeader() + (getVisibleRows() * 18) + 22, 240, ty * 16, 16, 16);
        }

        searchField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7 + getTabDelta(), t(grid.getGuiTitle()));
        drawString(7, getYPlayerInventory() - 12, t("container.inventory"));

        int x = 8;
        int y = 19 + getTabDelta();

        this.slotNumber = -1;

        int slot = scrollbar.getOffset() * 9;

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

        if (isOverSlotWithItem()) {
            drawTooltip(mouseX, mouseY, STACKS.get(slotNumber).getTooltip());
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
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedButton);

        searchField.mouseClicked(mouseX, mouseY, clickedButton);

        if (tabHovering >= 0 && tabHovering < grid.getTabs().size()) {
            grid.onTabSelectionChanged(tabHovering);
        }

        if (clickedButton == 1 && inBounds(79, 5 + getTabDelta(), 90, 12, mouseX - guiLeft, mouseY - guiTop)) {
            searchField.setText("");
            searchField.setFocused(true);

            sortItems();

            updateJEI();
        }

        boolean clickedClear = clickedButton == 0 && isOverClear(mouseX - guiLeft, mouseY - guiTop + getTabDelta());
        boolean clickedCreatePattern = clickedButton == 0 && isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop + getTabDelta());

        if (clickedCreatePattern) {
            BlockPos gridPos = ((TileGrid) grid).getPos();

            RS.INSTANCE.network.sendToServer(new MessageGridPatternCreate(gridPos.getX(), gridPos.getY(), gridPos.getZ()));
        } else if (grid.isActive()) {
            if (clickedClear) {
                RS.INSTANCE.network.sendToServer(new MessageGridCraftingClear((TileGrid) grid));
            }

            ItemStack held = ((ContainerGrid) this.inventorySlots).getPlayer().inventory.getItemStack();

            if (isOverSlotArea(mouseX - guiLeft, mouseY - guiTop) && !held.isEmpty() && (clickedButton == 0 || clickedButton == 1)) {
                RS.INSTANCE.network.sendToServer(grid.getType() == EnumGridType.FLUID ? new MessageGridFluidInsertHeld() : new MessageGridItemInsertHeld(clickedButton == 1));
            }

            if (isOverSlotWithItem()) {
                if (grid.getType() != EnumGridType.FLUID && (held.isEmpty() || (!held.isEmpty() && clickedButton == 2))) {
                    GridStackItem stack = (GridStackItem) STACKS.get(slotNumber);

                    if (stack.isCraftable() && (stack.doesDisplayCraftText() || (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()))) {
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
                } else if (grid.getType() == EnumGridType.FLUID && held.isEmpty()) {
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
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    private void updateJEI() {
        if (IntegrationJEI.isLoaded() && (grid.getSearchBoxMode() == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED || grid.getSearchBoxMode() == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED)) {
            RSJEIPlugin.INSTANCE.getRuntime().getItemListOverlay().setFilterText(searchField.getText());
        }
    }

    public void updateSearchFieldFocus(int mode) {
        if (searchField != null) {
            searchField.setCanLoseFocus(!TileGrid.isSearchBoxModeWithAutoselection(mode));
            searchField.setFocused(TileGrid.isSearchBoxModeWithAutoselection(mode));
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
}
