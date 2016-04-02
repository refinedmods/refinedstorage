package refinedstorage.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import refinedstorage.RefinedStorage;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.gui.sidebutton.SideButtonGridSearchBoxMode;
import refinedstorage.gui.sidebutton.SideButtonGridSortingDirection;
import refinedstorage.gui.sidebutton.SideButtonGridSortingType;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.jei.PluginRefinedStorage;
import refinedstorage.network.MessageGridCraftingClear;
import refinedstorage.network.MessageGridCraftingPush;
import refinedstorage.network.MessageStoragePull;
import refinedstorage.network.MessageStoragePush;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.TileController;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;

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
        super(container, 193, grid.getType() == EnumGridType.CRAFTING ? 256 : 208);

        this.container = container;
        this.grid = grid;
        this.scrollbar = new Scrollbar(174, 20, 12, grid.getType() == EnumGridType.CRAFTING ? 70 : 88);
    }

    @Override
    public void init(int x, int y) {
        if (grid.getRedstoneModeSetting() != null) {
            addSideButton(new SideButtonRedstoneMode(grid.getRedstoneModeSetting()));
        }

        addSideButton(new SideButtonGridSortingDirection(grid));
        addSideButton(new SideButtonGridSortingType(grid));

        if (PluginRefinedStorage.isJeiLoaded()) {
            addSideButton(new SideButtonGridSearchBoxMode(grid));
        }

        searchField = new GuiTextField(0, fontRendererObj, x + 80 + 1, y + 6 + 1, 88 - 6, fontRendererObj.FONT_HEIGHT);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setVisible(true);
        searchField.setTextColor(16777215);
        searchField.setCanLoseFocus(false);
        searchField.setFocused(true);
    }

    @Override
    public void update(int x, int y) {
        items.clear();

        if (grid.isConnected()) {
            items.addAll(grid.getController().getItemGroups());

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
        }

        return false;
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        if (grid.getType() == EnumGridType.CRAFTING) {
            bindTexture("gui/crafting_grid.png");
        } else {
            bindTexture("gui/grid.png");
        }

        drawTexture(x, y, 0, 0, width, height);

        scrollbar.draw(this);

        searchField.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        scrollbar.update(this, mouseX, mouseY);

        drawString(7, 7, t(grid.isWireless() ? "gui.refinedstorage:wireless_grid" : "gui.refinedstorage:grid"));

        if (grid.getType() == EnumGridType.CRAFTING) {
            drawString(7, 94, t("container.crafting"));
        }

        drawString(7, grid.getType() == EnumGridType.CRAFTING ? 163 : 113, t("container.inventory"));

        int x = 8;
        int y = 20;

        hoveringSlot = -1;

        int slot = getOffset() * 9;

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 9 * getVisibleRows(); ++i) {
            if (inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isConnected()) {
                hoveringSlot = slot;

                if (slot < items.size()) {
                    // we need to use the ID, because if we filter, the client-side index will change
                    // while the server-side's index will still be the same.
                    hoveringItemId = items.get(slot).getId();
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
                } else {
                    text = String.valueOf(qty);
                }

                if (hoveringSlot == slot && GuiScreen.isShiftKeyDown() && qty > 1) {
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
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedButton);

        boolean clickedClear = clickedButton == 0 && isHoveringOverClear(mouseX - guiLeft, mouseY - guiTop);

        if (grid.isConnected()) {
            TileController controller = grid.getController();

            if (isHoveringOverSlot() && container.getPlayer().inventory.getItemStack() != null && (clickedButton == 0 || clickedButton == 1)) {
                RefinedStorage.NETWORK.sendToServer(new MessageStoragePush(controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ(), -1, clickedButton == 1));
            } else if (isHoveringOverItemInSlot() && container.getPlayer().inventory.getItemStack() == null) {
                int flags = 0;

                if (clickedButton == 1) {
                    flags |= MessageStoragePull.PULL_HALF;
                }
                if (GuiScreen.isShiftKeyDown()) {
                    flags |= MessageStoragePull.PULL_SHIFT;
                }
                if (clickedButton == 2) {
                    flags |= MessageStoragePull.PULL_ONE;
                }

                RefinedStorage.NETWORK.sendToServer(new MessageStoragePull(controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ(), hoveringItemId, flags));
            } else if (clickedClear) {
                RefinedStorage.NETWORK.sendToServer(new MessageGridCraftingClear((TileGrid) grid));
            } else {
                for (Slot slot : container.getPlayerInventorySlots()) {
                    if (inBounds(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX - guiLeft, mouseY - guiTop)) {
                        if (GuiScreen.isShiftKeyDown()) {
                            RefinedStorage.NETWORK.sendToServer(new MessageStoragePush(controller.getPos().getX(), controller.getPos().getY(), controller.getPos().getZ(), slot.slotNumber, clickedButton == 1));
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

        if (clickedClear) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ui_button_click, 1.0F));
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!checkHotbarKeys(keyCode) && searchField.textboxKeyTyped(character, keyCode)) {
            if (PluginRefinedStorage.isJeiLoaded() && grid.getSearchBoxMode() == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED) {
                PluginRefinedStorage.INSTANCE.getRuntime().getItemListOverlay().setFilterText(searchField.getText());
            }
        } else {
            super.keyTyped(character, keyCode);
        }
    }

    public int getVisibleRows() {
        return grid.getType() == EnumGridType.CRAFTING ? 4 : 5;
    }
}
