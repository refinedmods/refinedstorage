package refinedstorage.gui.grid;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.FMLCommonHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.api.network.grid.IItemGridHandler;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.gui.GuiBase;
import refinedstorage.gui.Scrollbar;
import refinedstorage.gui.grid.sorting.GridSortingName;
import refinedstorage.gui.grid.sorting.GridSortingQuantity;
import refinedstorage.gui.grid.stack.ClientStackFluid;
import refinedstorage.gui.grid.stack.ClientStackItem;
import refinedstorage.gui.grid.stack.IClientStack;
import refinedstorage.gui.sidebutton.*;
import refinedstorage.integration.jei.IntegrationJEI;
import refinedstorage.integration.jei.RefinedStorageJEIPlugin;
import refinedstorage.network.*;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GuiGrid extends GuiBase {
    public static final GridSortingQuantity SORTING_QUANTITY = new GridSortingQuantity();
    public static final GridSortingName SORTING_NAME = new GridSortingName();

    public static final ListMultimap<Item, ClientStackItem> ITEMS = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());
    public static final ListMultimap<Fluid, ClientStackFluid> FLUIDS = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    public static List<IClientStack> STACKS = new ArrayList<>();

    private static boolean markedForSorting;

    private boolean wasConnected;

    private GuiTextField searchField;

    private ContainerGrid container;
    private IGrid grid;

    private int slotNumber;

    public static void markForSorting() {
        markedForSorting = true;
    }

    public GuiGrid(ContainerGrid container, IGrid grid) {
        super(container, grid.getType() == EnumGridType.FLUID ? 193 : 227, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 247 : 208);

        this.container = container;
        this.grid = grid;
        this.wasConnected = grid.isConnected();

        this.scrollbar = new Scrollbar(174, 20, 12, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN || grid.getType() == EnumGridType.FLUID) ? 70 : 88);
    }

    @Override
    public void init(int x, int y) {
        if (grid.getRedstoneModeConfig() != null) {
            addSideButton(new SideButtonRedstoneMode(this, grid.getRedstoneModeConfig()));
        }

        int sx = x + 80 + 1;
        int sy = y + 6 + 1;

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

        if (grid.getType() != EnumGridType.FLUID) {
            addSideButton(new SideButtonGridViewType(this, grid));
        }

        addSideButton(new SideButtonGridSortingDirection(this, grid));
        addSideButton(new SideButtonGridSortingType(this, grid));
        addSideButton(new SideButtonGridSearchBoxMode(this));

        sortItems();
    }

    public IGrid getGrid() {
        return grid;
    }

    private void sortItems() {
        List<IClientStack> stacks = new ArrayList<>();

        if (grid.isConnected()) {
            stacks.addAll(grid.getType() == EnumGridType.FLUID ? FLUIDS.values() : ITEMS.values());

            String query = searchField.getText().trim().toLowerCase();

            Iterator<IClientStack> t = stacks.iterator();

            while (t.hasNext()) {
                IClientStack stack = t.next();

                if (grid.getType() != EnumGridType.FLUID) {
                    List<GridFilteredItem> filteredItems = grid.getFilteredItems();

                    boolean found = filteredItems.isEmpty();

                    for (GridFilteredItem filteredItem : filteredItems) {
                        if (CompareUtils.compareStack(((ClientStackItem) stack).getStack(), filteredItem.getStack(), filteredItem.getCompare())) {
                            found = true;

                            break;
                        }
                    }

                    if (!found) {
                        t.remove();

                        continue;
                    }

                    if (grid.getViewType() == TileGrid.VIEW_TYPE_NON_CRAFTABLES && ((ClientStackItem) stack).isCraftable()) {
                        t.remove();

                        continue;
                    } else if (grid.getViewType() == TileGrid.VIEW_TYPE_CRAFTABLES && !((ClientStackItem) stack).isCraftable()) {
                        t.remove();

                        continue;
                    }
                }

                if (query.startsWith("@")) {
                    String[] parts = query.split(" ");

                    String modId = parts[0].substring(1);
                    String modIdFromItem = stack.getModId();

                    if (!modIdFromItem.contains(modId)) {
                        t.remove();
                    } else if (parts.length >= 2) {
                        StringBuilder itemFromMod = new StringBuilder();

                        for (int i = 1; i < parts.length; ++i) {
                            itemFromMod.append(parts[i]);

                            if (i != parts.length - 1) {
                                itemFromMod.append(" ");
                            }
                        }

                        if (!stack.getName().toLowerCase().contains(itemFromMod.toString())) {
                            t.remove();
                        }
                    }
                } else if (!stack.getName().toLowerCase().contains(query)) {
                    t.remove();
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
        if (wasConnected != grid.isConnected()) {
            wasConnected = grid.isConnected();

            markForSorting();
        }

        if (markedForSorting) {
            markedForSorting = false;

            sortItems();
        }
    }

    private int getRows() {
        return Math.max(0, (int) Math.ceil((float) STACKS.size() / 9F));
    }

    private boolean isOverSlotWithItem() {
        return grid.isConnected() && isOverSlot() && slotNumber < STACKS.size();
    }

    private boolean isOverSlot() {
        return slotNumber >= 0;
    }

    private boolean isOverSlotArea(int mouseX, int mouseY) {
        return inBounds(7, 19, 162, 18 * getVisibleRows(), mouseX, mouseY);
    }

    private int getVisibleRows() {
        return (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 4 : 5;
    }

    private boolean isOverClear(int mouseX, int mouseY) {
        switch (grid.getType()) {
            case CRAFTING:
                return inBounds(82, 95, 7, 7, mouseX, mouseY);
            case PATTERN:
                return inBounds(64, 95, 7, 7, mouseX, mouseY);
            default:
                return false;
        }
    }

    private boolean isOverCreatePattern(int mouseX, int mouseY) {
        return grid.getType() == EnumGridType.PATTERN && inBounds(152, 114, 16, 16, mouseX, mouseY) && ((TileGrid) grid).canCreatePattern();
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        if (grid.getType() == EnumGridType.CRAFTING) {
            bindTexture("gui/crafting_grid.png");
        } else if (grid.getType() == EnumGridType.PATTERN) {
            bindTexture("gui/pattern_grid.png");
        } else {
            bindTexture("gui/grid.png");
        }

        drawTexture(x, y, 0, 0, width, height);

        if (grid.getType() == EnumGridType.PATTERN) {
            int ty = 0;

            if (isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
                ty = 1;
            }

            if (!((TileGrid) grid).canCreatePattern()) {
                ty = 2;
            }

            drawTexture(x + 152, y + 114, 240, ty * 16, 16, 16);
        }

        searchField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t(grid.getGuiTitle()));
        drawString(7, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 153 : 114, t("container.inventory"));

        int x = 8;
        int y = 20;

        this.slotNumber = -1;

        int slot = scrollbar.getOffset() * 9;

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 9 * getVisibleRows(); ++i) {
            if (inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isConnected()) {
                this.slotNumber = slot;
            }

            if (slot < STACKS.size()) {
                STACKS.get(slot).draw(this, x, y, GuiScreen.isShiftKeyDown() && slotNumber == slot);
            }

            if (inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isConnected()) {
                int color = grid.isConnected() ? -2130706433 : 0xFF5B5B5B;

                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                zLevel = 190;
                GlStateManager.colorMask(true, true, true, false);
                drawGradientRect(x, y, x + 16, y + 16, color, color);
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
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedButton);

        searchField.mouseClicked(mouseX, mouseY, clickedButton);

        if (clickedButton == 1 && inBounds(79, 5, 90, 12, mouseX - guiLeft, mouseY - guiTop)) {
            searchField.setText("");
            searchField.setFocused(true);

            sortItems();

            updateJEI();
        }

        boolean clickedClear = clickedButton == 0 && isOverClear(mouseX - guiLeft, mouseY - guiTop);
        boolean clickedCreatePattern = clickedButton == 0 && isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop);

        if (clickedCreatePattern) {
            BlockPos gridPos = ((TileGrid) grid).getPos();

            RefinedStorage.INSTANCE.network.sendToServer(new MessageGridPatternCreate(gridPos.getX(), gridPos.getY(), gridPos.getZ()));
        } else if (grid.isConnected()) {
            if (clickedClear) {
                RefinedStorage.INSTANCE.network.sendToServer(new MessageGridCraftingClear((TileGrid) grid));
            }

            ItemStack held = container.getPlayer().inventory.getItemStack();

            if (isOverSlotArea(mouseX - guiLeft, mouseY - guiTop) && held != null && (clickedButton == 0 || clickedButton == 1)) {
                RefinedStorage.INSTANCE.network.sendToServer(grid.getType() == EnumGridType.FLUID ? new MessageGridFluidInsertHeld() : new MessageGridItemInsertHeld(clickedButton == 1));
            }

            if (isOverSlotWithItem()) {
                if (grid.getType() != EnumGridType.FLUID && (held == null || (held != null && clickedButton == 2))) {
                    ClientStackItem stack = (ClientStackItem) STACKS.get(slotNumber);

                    if (stack.isCraftable() && (stack.getQuantity() == 0 || (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()))) {
                        FMLCommonHandler.instance().showGuiScreen(new GuiCraftingStart(this, container.getPlayer(), stack));
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

                        RefinedStorage.INSTANCE.network.sendToServer(new MessageGridItemPull(stack.getHash(), flags));
                    }
                } else if (grid.getType() == EnumGridType.FLUID && held == null) {
                    RefinedStorage.INSTANCE.network.sendToServer(new MessageGridFluidPull(STACKS.get(slotNumber).getHash(), GuiScreen.isShiftKeyDown()));
                }
            }
        }

        if (clickedClear || clickedCreatePattern) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
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
            RefinedStorageJEIPlugin.INSTANCE.getRuntime().getItemListOverlay().setFilterText(searchField.getText());
        }
    }

    public void updateSearchFieldFocus(int mode) {
        if (searchField != null) {
            searchField.setCanLoseFocus(!TileGrid.isSearchBoxModeWithAutoselection(mode));
            searchField.setFocused(TileGrid.isSearchBoxModeWithAutoselection(mode));
        }
    }
}
