package refinedstorage.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.gui.sidebutton.SideButtonGridSearchBoxMode;
import refinedstorage.gui.sidebutton.SideButtonGridSortingDirection;
import refinedstorage.gui.sidebutton.SideButtonGridSortingType;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.jei.RefinedStorageJEIPlugin;
import refinedstorage.network.GridPullFlags;
import refinedstorage.network.MessageGridCraftingClear;
import refinedstorage.network.MessageGridCraftingPush;
import refinedstorage.network.MessageGridPatternCreate;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;
import refinedstorage.tile.grid.WirelessGrid;

import java.io.IOException;
import java.util.*;

public class GuiGrid extends GuiBase {
    private ContainerGrid container;
    private IGrid grid;

    private List<ItemGroup> items = new ArrayList<ItemGroup>();

    private GuiTextField searchField;

    private int hoveringSlot;
    private int hoveringItemId;

    private Scrollbar scrollbar;

    public GuiGrid(ContainerGrid container, IGrid grid) {
        super(container, 193, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 256 : 208);

        this.container = container;
        this.grid = grid;
        this.scrollbar = new Scrollbar(174, 20, 12, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 70 : 88);
    }

    @Override
    public void init(int x, int y) {
        if (grid.getRedstoneModeSetting() != null) {
            addSideButton(new SideButtonRedstoneMode(grid.getRedstoneModeSetting()));
        }

        searchField = new GuiTextField(0, fontRendererObj, x + 80 + 1, y + 6 + 1, 88 - 6, fontRendererObj.FONT_HEIGHT);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setVisible(true);
        searchField.setTextColor(16777215);
        searchField.setCanLoseFocus(!TileGrid.isSearchBoxModeWithAutoselection(grid.getSearchBoxMode()));
        searchField.setFocused(TileGrid.isSearchBoxModeWithAutoselection(grid.getSearchBoxMode()));

        addSideButton(new SideButtonGridSortingDirection(grid));
        addSideButton(new SideButtonGridSortingType(grid));
        addSideButton(new SideButtonGridSearchBoxMode(grid));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        scrollbar.update(this, mouseX - guiLeft, mouseY - guiTop);
    }

    public IGrid getGrid() {
        return grid;
    }

    @Override
    public void update(int x, int y) {
        items.clear();

        if (grid.isConnected()) {
            items.addAll(grid.getItemGroups());

            if (!searchField.getText().trim().isEmpty()) {
                Iterator<ItemGroup> t = items.iterator();

                while (t.hasNext()) {
                    ItemGroup group = t.next();

                    if (!group.toItemStack().getDisplayName().toLowerCase().contains(searchField.getText().toLowerCase())) {
                        t.remove();
                    }
                }
            }

            Collections.sort(items, new Comparator<ItemGroup>() {
                @Override
                public int compare(ItemGroup left, ItemGroup right) {
                    if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_ASCENDING) {
                        return right.toItemStack().getDisplayName().compareTo(left.toItemStack().getDisplayName());
                    } else if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_DESCENDING) {
                        return left.toItemStack().getDisplayName().compareTo(right.toItemStack().getDisplayName());
                    }

                    return 0;
                }
            });

            if (grid.getSortingType() == TileGrid.SORTING_TYPE_QUANTITY) {
                Collections.sort(items, new Comparator<ItemGroup>() {
                    @Override
                    public int compare(ItemGroup left, ItemGroup right) {
                        if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_ASCENDING) {
                            return Integer.valueOf(right.getQuantity()).compareTo(left.getQuantity());
                        } else if (grid.getSortingDirection() == TileGrid.SORTING_DIRECTION_DESCENDING) {
                            return Integer.valueOf(left.getQuantity()).compareTo(right.getQuantity());
                        }

                        return 0;
                    }
                });
            }
        }

        scrollbar.setCanScroll(getRows() > getVisibleRows());
        scrollbar.setScrollDelta((float) scrollbar.getScrollbarHeight() / (float) getRows());
    }

    public int getOffset() {
        return (int) (scrollbar.getCurrentScroll() / 70f * (float) getRows());
    }

    public int getRows() {
        int max = (int) Math.ceil((float) items.size() / (float) 9);

        return max < 0 ? 0 : max;
    }

    private boolean isHoveringOverItemInSlot() {
        return grid.isConnected() && isHoveringOverSlot() && hoveringSlot < items.size();
    }

    private boolean isHoveringOverSlot() {
        return hoveringSlot >= 0;
    }

    public boolean isHoveringOverClear(int mouseX, int mouseY) {
        if (grid.getType() == EnumGridType.CRAFTING) {
            return inBounds(81, 105, 7, 7, mouseX, mouseY);
        } else if (grid.getType() == EnumGridType.PATTERN) {
            return inBounds(64, 105, 7, 7, mouseX, mouseY);
        }

        return false;
    }

    public boolean isHoveringOverCreatePattern(int mouseX, int mouseY) {
        if (grid.getType() == EnumGridType.PATTERN) {
            return inBounds(152, 124, 16, 16, mouseX, mouseY) && ((TileGrid) grid).canCreatePattern();
        }

        return false;
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

            if (isHoveringOverCreatePattern(mouseX - guiLeft, mouseY - guiTop)) {
                ty = 1;
            }

            if (!((TileGrid) grid).canCreatePattern()) {
                ty = 2;
            }

            drawTexture(x + 152, y + 124, 195, ty * 16, 16, 16);
        }

        scrollbar.draw(this);

        searchField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 8, t(grid instanceof WirelessGrid ? "gui.refinedstorage:wireless_grid" : "gui.refinedstorage:grid"));

        if (grid.getType() == EnumGridType.CRAFTING) {
            drawString(7, 94, t("container.crafting"));
        } else if (grid.getType() == EnumGridType.PATTERN) {
            drawString(7, 94, t("gui.refinedstorage:grid.pattern"));
        }

        drawString(7, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 163 : 114, t("container.inventory"));

        int x = 8;
        int y = 20;

        this.hoveringSlot = -1;

        int slot = getOffset() * 9;

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 9 * getVisibleRows(); ++i) {
            if (inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isConnected()) {
                this.hoveringSlot = slot;

                if (slot < items.size()) {
                    // we need to use the ID, because if we filter, the client-side index will change
                    // while the server-side's index will still be the same.
                    this.hoveringItemId = items.get(slot).getId();
                }
            }

            if (slot < items.size()) {
                int qty = items.get(slot).getQuantity();

                String text;

                if (qty >= 1000000) {
                    text = String.format("%.1f", (float) qty / 1000000).replace(",", ".").replace(".0", "") + "M";
                } else if (qty >= 1000) {
                    text = String.format("%.1f", (float) qty / 1000).replace(",", ".").replace(".0", "") + "K";
                } else if (qty == 1) {
                    text = null;
                } else if (qty == 0) {
                    text = t("gui.refinedstorage:grid.craft");
                } else {
                    text = String.valueOf(qty);
                }

                if (this.hoveringSlot == slot && GuiScreen.isShiftKeyDown() && qty > 1) {
                    text = String.valueOf(qty);
                }

                drawItem(x, y, items.get(slot).toItemStack(), true, text);
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

        if (isHoveringOverItemInSlot()) {
            drawTooltip(mouseX, mouseY, items.get(hoveringSlot).toItemStack());
        }

        if (isHoveringOverClear(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("misc.refinedstorage:clear"));
        }

        if (isHoveringOverCreatePattern(mouseX, mouseY)) {
            drawTooltip(mouseX, mouseY, t("gui.refinedstorage:grid.pattern_create"));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedButton);

        searchField.mouseClicked(mouseX, mouseY, clickedButton);

        boolean clickedClear = clickedButton == 0 && isHoveringOverClear(mouseX - guiLeft, mouseY - guiTop);
        boolean clickedCreatePattern = clickedButton == 0 && isHoveringOverCreatePattern(mouseX - guiLeft, mouseY - guiTop);

        boolean playClickSound = clickedClear || clickedCreatePattern;

        if (grid.isConnected()) {
            if (clickedCreatePattern) {
                BlockPos gridPos = ((TileGrid) grid).getPos();
                RefinedStorage.NETWORK.sendToServer(new MessageGridPatternCreate(gridPos.getX(), gridPos.getY(), gridPos.getZ()));
            } else if (isHoveringOverSlot() && container.getPlayer().inventory.getItemStack() != null && (clickedButton == 0 || clickedButton == 1)) {
                grid.onItemPush(-1, clickedButton == 1);
            } else if (isHoveringOverItemInSlot() && container.getPlayer().inventory.getItemStack() == null) {
                if (items.get(hoveringSlot).getQuantity() == 0 || (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown())) {
                    FMLCommonHandler.instance().showGuiScreen(new GuiCraftingSettings(this, hoveringItemId));
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

                    grid.onItemPull(hoveringItemId, flags);
                }
            } else if (clickedClear && grid.isConnected()) {
                RefinedStorage.NETWORK.sendToServer(new MessageGridCraftingClear((TileGrid) grid));
            } else {
                for (Slot slot : container.getPlayerInventorySlots()) {
                    if (inBounds(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX - guiLeft, mouseY - guiTop)) {
                        if (GuiScreen.isShiftKeyDown()) {
                            grid.onItemPush(slot.slotNumber, clickedButton == 1);
                        }
                    }
                }

                if (grid.getType() == EnumGridType.CRAFTING) {
                    for (Slot slot : container.getCraftingSlots()) {
                        if (inBounds(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX - guiLeft, mouseY - guiTop)) {
                            if (GuiScreen.isShiftKeyDown()) {
                                RefinedStorage.NETWORK.sendToServer(new MessageGridCraftingPush((TileGrid) grid, slot.getSlotIndex()));
                            }
                        }
                    }
                }
            }
        }

        if (playClickSound) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && searchField.textboxKeyTyped(character, keyCode)) {
            if (RefinedStorage.hasJei() && (grid.getSearchBoxMode() == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED || grid.getSearchBoxMode() == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED)) {
                RefinedStorageJEIPlugin.INSTANCE.getRuntime().getItemListOverlay().setFilterText(searchField.getText());
            }
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    public int getVisibleRows() {
        return (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 4 : 5;
    }
}
