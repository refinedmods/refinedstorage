package refinedstorage.gui;

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
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.gui.sidebutton.SideButtonGridSearchBoxMode;
import refinedstorage.gui.sidebutton.SideButtonGridSortingDirection;
import refinedstorage.gui.sidebutton.SideButtonGridSortingType;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.jei.RefinedStorageJEIPlugin;
import refinedstorage.network.*;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;
import refinedstorage.tile.grid.WirelessGrid;

import java.io.IOException;
import java.util.*;

public class GuiGrid extends GuiBase {
    private Comparator<ItemStack> quantityComparator = new Comparator<ItemStack>() {
        @Override
        public int compare(ItemStack left, ItemStack right) {
            int leftSize = left.stackSize;
            int rightSize = right.stackSize;

            if (leftSize == rightSize) {
                return 0;
            }

            if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_ASCENDING) {
                return (leftSize > rightSize) ? 1 : -1;
            } else if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_DESCENDING) {
                return (rightSize > leftSize) ? 1 : -1;
            }

            return 0;
        }
    };

    private Comparator<ItemStack> nameComparator = new Comparator<ItemStack>() {
        @Override
        public int compare(ItemStack left, ItemStack right) {
            if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_ASCENDING) {
                return left.getDisplayName().compareTo(right.getDisplayName());
            } else if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_DESCENDING) {
                return right.getDisplayName().compareTo(left.getDisplayName());
            }

            return 0;
        }
    };

    private GuiTextField searchField;

    private ContainerGrid container;
    private List<ItemStack> items = new ArrayList<ItemStack>();
    private IGrid grid;

    private int slotNumber;

    public GuiGrid(ContainerGrid container, IGrid grid) {
        super(container, 193, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 256 : 208);

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

        if (searchField == null) {
            searchField = new GuiTextField(0, fontRendererObj, sx, sy, 88 - 6, fontRendererObj.FONT_HEIGHT);
            searchField.setEnableBackgroundDrawing(false);
            searchField.setVisible(true);
            searchField.setTextColor(16777215);

            updateSearchBoxFocus(grid.getSearchBoxMode());
        } else {
            searchField.xPosition = sx;
            searchField.yPosition = sy;
        }

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
            items.addAll(grid.getItems());

            String query = searchField.getText().trim().toLowerCase();

            if (!query.isEmpty()) {
                Iterator<ItemStack> t = items.iterator();

                while (t.hasNext()) {
                    ItemStack item = t.next();

                    if (query.startsWith("@")) {
                        String[] parts = query.split(" ");

                        String modId = parts[0].substring(1);
                        String modIdFromItem = Item.REGISTRY.getNameForObject(item.getItem()).getResourceDomain();

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

                            if (!item.getDisplayName().toLowerCase().contains(itemFromMod.toString())) {
                                t.remove();
                            }
                        }
                    } else if (query.startsWith("#")) {
                        String tooltip = query.substring(1);
                        String tooltipFromItem = StringUtils.join(item.getTooltip(container.getPlayer(), true), "\n");

                        if (!tooltipFromItem.contains(tooltip)) {
                            t.remove();
                        }
                    } else if (!item.getDisplayName().toLowerCase().contains(query)) {
                        t.remove();
                    }
                }
            }

            Collections.sort(items, nameComparator);

            if (grid.getSortingType() == TileGrid.SORTING_TYPE_QUANTITY) {
                Collections.sort(items, quantityComparator);
            }
        }

        getScrollbar().setCanScroll(getRows() > getVisibleRows());
        getScrollbar().setScrollDelta((float) getScrollbar().getScrollbarHeight() / (float) getRows());
    }

    public int getOffset() {
        return (int) Math.ceil(getScrollbar().getCurrentScroll() / 70f * (float) getRows());
    }

    public int getRows() {
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

    public int getVisibleRows() {
        return (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 4 : 5;
    }

    public boolean isOverClear(int mouseX, int mouseY) {
        switch (grid.getType()) {
            case CRAFTING:
                return inBounds(81, 105, 7, 7, mouseX, mouseY);
            case PATTERN:
                return inBounds(64, 105, 7, 7, mouseX, mouseY);
            default:
                return false;
        }
    }

    public boolean isOverCreatePattern(int mouseX, int mouseY) {
        return grid.getType() == EnumGridType.PATTERN && inBounds(152, 124, 16, 16, mouseX, mouseY) && ((TileGrid) grid).canCreatePattern();
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

            drawTexture(x + 152, y + 124, 195, ty * 16, 16, 16);
        }

        searchField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t(grid instanceof WirelessGrid ? "gui.refinedstorage:wireless_grid" : "gui.refinedstorage:grid"));

        if (grid.getType() == EnumGridType.CRAFTING) {
            drawString(7, 95, t("container.crafting"));
        } else if (grid.getType() == EnumGridType.PATTERN) {
            drawString(7, 95, t("gui.refinedstorage:grid.pattern"));
        }

        drawString(7, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 163 : 114, t("container.inventory"));

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
                ItemStack stack = items.get(slot);

                drawItem(x, y, stack, true, formatQuantity(stack.stackSize, slot));
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
            drawTooltip(mouseX, mouseY, items.get(slotNumber));
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

        searchField.mouseClicked(mouseX, mouseY, clickedButton);

        if (clickedButton == 1 && inBounds(79, 5, 90, 12, mouseX - guiLeft, mouseY - guiTop)) {
            searchField.setText("");
            searchField.setFocused(true);

            updateJEI();
        }

        boolean clickedClear = clickedButton == 0 && isOverClear(mouseX - guiLeft, mouseY - guiTop);
        boolean clickedCreatePattern = clickedButton == 0 && isOverCreatePattern(mouseX - guiLeft, mouseY - guiTop);

        if (clickedCreatePattern) {
            BlockPos gridPos = ((TileGrid) grid).getPos();

            RefinedStorage.NETWORK.sendToServer(new MessageGridPatternCreate(gridPos.getX(), gridPos.getY(), gridPos.getZ()));
        } else if (grid.isConnected()) {
            if (clickedClear) {
                RefinedStorage.NETWORK.sendToServer(new MessageGridCraftingClear((TileGrid) grid));
            }

            if (isOverSlotArea(mouseX - guiLeft, mouseY - guiTop) && container.getPlayer().inventory.getItemStack() != null && (clickedButton == 0 || clickedButton == 1)) {
                RefinedStorage.NETWORK.sendToServer(new MessageGridHeldPush(clickedButton == 1));
            }

            if (isOverSlotWithItem() && container.getPlayer().inventory.getItemStack() == null) {
                if (items.get(slotNumber).stackSize == 0 || (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown())) {
                    FMLCommonHandler.instance().showGuiScreen(new GuiCraftingSettings(this, container.getPlayer(), items.get(slotNumber)));
                } else {
                    int flags = 0;

                    if (clickedButton == 1) {
                        flags |= GridPullFlags.PULL_HALF;
                    }

                    if (GuiScreen.isShiftKeyDown()) {
                        flags |= GridPullFlags.PULL_SHIFT;
                    }

                    if (clickedButton == 2) {
                        flags |= GridPullFlags.PULL_ONE;
                    }

                    RefinedStorage.NETWORK.sendToServer(new MessageGridPull(items.get(slotNumber), flags));
                }
            }
        }

        if (clickedClear || clickedCreatePattern) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && searchField.textboxKeyTyped(character, keyCode)) {
            updateJEI();
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    private void updateJEI() {
        if (RefinedStorage.hasJei() && (grid.getSearchBoxMode() == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED || grid.getSearchBoxMode() == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED)) {
            RefinedStorageJEIPlugin.INSTANCE.getRuntime().getItemListOverlay().setFilterText(searchField.getText());
        }
    }

    public void updateSearchBoxFocus(int mode) {
        searchField.setCanLoseFocus(!TileGrid.isSearchBoxModeWithAutoselection(mode));
        searchField.setFocused(TileGrid.isSearchBoxModeWithAutoselection(mode));
    }
}
