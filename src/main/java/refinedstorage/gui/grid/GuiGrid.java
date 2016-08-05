package refinedstorage.gui.grid;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.StringUtils;
import refinedstorage.RefinedStorage;
import refinedstorage.api.network.GridExtractFlags;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.gui.GuiBase;
import refinedstorage.gui.Scrollbar;
import refinedstorage.gui.grid.sorting.GridSortingName;
import refinedstorage.gui.grid.sorting.GridSortingQuantity;
import refinedstorage.gui.sidebutton.*;
import refinedstorage.integration.jei.IntegrationJEI;
import refinedstorage.network.MessageGridCraftingClear;
import refinedstorage.network.MessageGridInsertHeld;
import refinedstorage.network.MessageGridPatternCreate;
import refinedstorage.network.MessageGridPull;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;
import refinedstorage.tile.grid.WirelessGrid;

import java.io.IOException;
import java.util.*;

public class GuiGrid extends GuiBase {
    private GridSortingQuantity quantitySorting = new GridSortingQuantity();
    private GridSortingName nameSorting = new GridSortingName();

    public static GuiTextField SEARCH_FIELD;

    private ContainerGrid container;
    private List<ClientStack> items = new ArrayList<>();
    private IGrid grid;

    private int slotNumber;

    public GuiGrid(ContainerGrid container, IGrid grid) {
        super(container, 227, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 247 : 208);

        setScrollbar(new Scrollbar(174, 20, 12, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 70 : 88));
        getScrollbar().setCanScroll(false);

        this.container = container;
        this.grid = grid;
    }

    @Override
    public void init(int x, int y) {
        if (grid.getRedstoneModeConfig() != null) {
            addSideButton(new SideButtonRedstoneMode(grid.getRedstoneModeConfig()));
        }

        int sx = x + 80 + 1;
        int sy = y + 6 + 1;

        if (SEARCH_FIELD == null) {
            SEARCH_FIELD = new GuiTextField(0, fontRendererObj, sx, sy, 88 - 6, fontRendererObj.FONT_HEIGHT);
            SEARCH_FIELD.setEnableBackgroundDrawing(false);
            SEARCH_FIELD.setVisible(true);
            SEARCH_FIELD.setTextColor(16777215);
            updateSearchFieldFocus(grid.getSearchBoxMode());
        } else {
            SEARCH_FIELD.xPosition = sx;
            SEARCH_FIELD.yPosition = sy;
        }

        addSideButton(new SideButtonGridViewType(grid));
        addSideButton(new SideButtonGridSortingDirection(grid));
        addSideButton(new SideButtonGridSortingType(grid));
        addSideButton(new SideButtonGridSearchBoxMode(this));
    }

    public IGrid getGrid() {
        return grid;
    }

    @Override
    public void update(int x, int y) {
        items.clear();

        if (grid.isConnected()) {
            items.addAll(RefinedStorage.INSTANCE.items);

            String query = SEARCH_FIELD.getText().trim().toLowerCase();

            Iterator<ClientStack> t = items.iterator();

            while (t.hasNext()) {
                ClientStack stack = t.next();

                List<GridFilteredItem> filteredItems = grid.getFilteredItems();

                boolean found = filteredItems.isEmpty();

                for (GridFilteredItem filteredItem : filteredItems) {
                    if (CompareUtils.compareStack(stack.getStack(), filteredItem.getStack(), filteredItem.getCompare())) {
                        found = true;

                        break;
                    }
                }

                if (!found) {
                    t.remove();

                    continue;
                }

                if (grid.getViewType() == TileGrid.VIEW_TYPE_NON_CRAFTABLES && stack.isCraftable()) {
                    t.remove();

                    continue;
                } else if (grid.getViewType() == TileGrid.VIEW_TYPE_CRAFTABLES && !stack.isCraftable()) {
                    t.remove();

                    continue;
                }

                if (query.startsWith("@")) {
                    String[] parts = query.split(" ");

                    String modId = parts[0].substring(1);
                    String modIdFromItem = Item.REGISTRY.getNameForObject(stack.getStack().getItem()).getResourceDomain();

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

                        if (!stack.getStack().getDisplayName().toLowerCase().contains(itemFromMod.toString())) {
                            t.remove();
                        }
                    }
                } else if (query.startsWith("#")) {
                    String tooltip = query.substring(1);
                    String tooltipFromItem = StringUtils.join(stack.getStack().getTooltip(container.getPlayer(), true), "\n");

                    if (!tooltipFromItem.contains(tooltip)) {
                        t.remove();
                    }
                } else if (!stack.getStack().getDisplayName().toLowerCase().contains(query)) {
                    t.remove();
                }
            }

            nameSorting.setSortingDirection(grid.getSortingDirection());
            quantitySorting.setSortingDirection(grid.getSortingDirection());

            Collections.sort(items, nameSorting);

            if (grid.getSortingType() == TileGrid.SORTING_TYPE_QUANTITY) {
                Collections.sort(items, quantitySorting);
            }
        }

        getScrollbar().setCanScroll(getRows() > getVisibleRows());
        getScrollbar().setScrollDelta((float) getScrollbar().getScrollbarHeight() / (float) getRows());
    }

    private int getOffset() {
        return (int) Math.ceil(getScrollbar().getCurrentScroll() / 70f * (float) getRows());
    }

    private int getRows() {
        int max = (int) Math.ceil((float) items.size() / 9f);

        return max < 0 ? 0 : max;
    }

    private boolean isOverSlotWithItem() {
        return grid.isConnected() && isOverSlot() && slotNumber < items.size();
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

        SEARCH_FIELD.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t(grid instanceof WirelessGrid ? "gui.refinedstorage:wireless_grid" : "gui.refinedstorage:grid"));
        drawString(7, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 153 : 114, t("container.inventory"));

        int x = 8;
        int y = 20;

        this.slotNumber = -1;

        int slot = getOffset() * 9;

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 9 * getVisibleRows(); ++i) {
            if (inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isConnected()) {
                this.slotNumber = slot;
            }

            if (slot < items.size()) {
                drawItem(x, y, items.get(slot).getStack(), true, formatQuantity(items.get(slot).getStack().stackSize, slot));
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
            drawTooltip(mouseX, mouseY, items.get(slotNumber).getStack());
        }

        if (isOverClear(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:clear"));
        }

        if (isOverCreatePattern(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("gui.refinedstorage:grid.pattern_create"));
        }
    }

    private String formatQuantity(int qty, int slot) {
        if (slotNumber == slot && GuiScreen.isShiftKeyDown() && qty > 1) {
            return String.valueOf(qty);
        }

        if (qty >= 1000000) {
            return String.format(Locale.US, "%.1f", (float) qty / 1000000).replace(".0", "") + "M";
        } else if (qty >= 1000) {
            return String.format(Locale.US, "%.1f", (float) qty / 1000).replace(".0", "") + "K";
        } else if (qty == 1) {
            return null;
        } else if (qty == 0) {
            return t("gui.refinedstorage:grid.craft");
        } else {
            return String.valueOf(qty);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedButton);

        SEARCH_FIELD.mouseClicked(mouseX, mouseY, clickedButton);

        if (clickedButton == 1 && inBounds(79, 5, 90, 12, mouseX - guiLeft, mouseY - guiTop)) {
            SEARCH_FIELD.setText("");
            SEARCH_FIELD.setFocused(true);

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
                RefinedStorage.INSTANCE.network.sendToServer(new MessageGridInsertHeld(clickedButton == 1));
            }

            if (isOverSlotWithItem() && (held == null || (held != null && clickedButton == 2))) {
                if (items.get(slotNumber).isCraftable() && (items.get(slotNumber).getStack().stackSize == 0 || (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()))) {
                    FMLCommonHandler.instance().showGuiScreen(new GuiCraftingSettings(this, container.getPlayer(), items.get(slotNumber)));
                } else {
                    int flags = 0;

                    if (clickedButton == 1) {
                        flags |= GridExtractFlags.EXTRACT_HALF;
                    }

                    if (GuiScreen.isShiftKeyDown()) {
                        flags |= GridExtractFlags.EXTRACT_SHIFT;
                    }

                    if (clickedButton == 2) {
                        flags |= GridExtractFlags.EXTRACT_SINGLE;
                    }

                    RefinedStorage.INSTANCE.network.sendToServer(new MessageGridPull(items.get(slotNumber).getId(), flags));
                }
            }
        }

        if (clickedClear || clickedCreatePattern) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && SEARCH_FIELD.textboxKeyTyped(character, keyCode)) {
            updateJEI();
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    private void updateJEI() {
        if (IntegrationJEI.isLoaded() && (grid.getSearchBoxMode() == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED || grid.getSearchBoxMode() == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED)) {
            IntegrationJEI.INSTANCE.getRuntime().getItemListOverlay().setFilterText(SEARCH_FIELD.getText());
        }
    }

    public static void updateSearchFieldFocus(int mode) {
        SEARCH_FIELD.setCanLoseFocus(!TileGrid.isSearchBoxModeWithAutoselection(mode));
        SEARCH_FIELD.setFocused(TileGrid.isSearchBoxModeWithAutoselection(mode));
    }
}
