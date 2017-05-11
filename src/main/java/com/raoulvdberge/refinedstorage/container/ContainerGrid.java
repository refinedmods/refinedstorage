package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.container.slot.*;
import com.raoulvdberge.refinedstorage.gui.grid.IGridDisplay;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class ContainerGrid extends ContainerBase {
    public static final int TAB_WIDTH = 28;
    public static final int TAB_HEIGHT = 31;

    private IGrid grid;
    private IGridDisplay display;

    private SlotGridCraftingResult craftingResultSlot;
    private SlotDisabled patternResultSlot;

    public ContainerGrid(IGrid grid, IGridDisplay display, @Nullable TileBase gridTile, EntityPlayer player) {
        super(gridTile, player);

        this.grid = grid;
        this.display = display;

        initSlots();
    }

    public void initSlots() {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();

        int headerAndSlots = getTabDelta() + display.getHeader() + (display.getVisibleRows() * 18);

        if (grid.getType() != GridType.FLUID) {
            int yStart = 6;

            if (grid instanceof IPortableGrid) {
                yStart = 38;
            }

            for (int i = 0; i < 4; ++i) {
                addSlotToContainer(new SlotItemHandler(grid.getFilter(), i, 204, yStart + (18 * i) + getTabDelta()));
            }
        }

        if (grid.getType() == GridType.PATTERN) {
            addSlotToContainer(new SlotItemHandler(((NetworkNodeGrid) grid).getPatterns(), 0, 152, headerAndSlots + 4));
            addSlotToContainer(new SlotOutput(((NetworkNodeGrid) grid).getPatterns(), 1, 152, headerAndSlots + 40));
        }

        if (grid instanceof IPortableGrid) {
            addSlotToContainer(new SlotItemHandler(((IPortableGrid) grid).getDisk(), 0, 204, 6 + getTabDelta()));
        }

        addPlayerInventory(8, display.getYPlayerInventory());

        if (grid.getType() == GridType.CRAFTING) {
            int x = 26;
            int y = headerAndSlots + 4;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotGridCrafting(grid.getCraftingMatrix(), i, x, y));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 26;
                }
            }

            addSlotToContainer(craftingResultSlot = new SlotGridCraftingResult(this, getPlayer(), (NetworkNodeGrid) grid, 0, 130 + 4, headerAndSlots + 22));
        } else if (grid.getType() == GridType.PATTERN) {
            int x = 8;
            int y = headerAndSlots + 4;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotFilterLegacy(grid.getCraftingMatrix(), i, x, y));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 8;
                }
            }

            addSlotToContainer(patternResultSlot = new SlotDisabled(grid.getCraftingResult(), 0, 112 + 4, headerAndSlots + 22));
        }
    }

    private int getTabDelta() {
        return !grid.getTabs().isEmpty() ? TAB_HEIGHT - 4 : 0;
    }

    public IGrid getGrid() {
        return grid;
    }

    public void sendCraftingSlots() {
        for (int i = 0; i < inventorySlots.size(); ++i) {
            Slot slot = inventorySlots.get(i);

            if (slot instanceof SlotGridCrafting || slot == craftingResultSlot) {
                for (IContainerListener listener : listeners) {
                    listener.sendSlotContents(this, i, slot.getStack());
                }
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        if (!player.getEntityWorld().isRemote) {
            grid.onClosed(player);
        }
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return (slot == craftingResultSlot || slot == patternResultSlot) ? false : super.canMergeSlot(stack, slot);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        if (!player.getEntityWorld().isRemote) {
            Slot slot = inventorySlots.get(slotIndex);

            if (slot.getHasStack()) {
                if (slot == craftingResultSlot) {
                    grid.onCraftedShift(player);

                    sendCraftingSlots();
                    detectAndSendChanges();
                } else if (slot != patternResultSlot && !(slot instanceof SlotFilterLegacy)) {
                    ItemStack stack = slot.getStack();

                    if (grid.getType() != GridType.FLUID && stack.getItem() == RSItems.FILTER) {
                        int startIndex = 0;
                        int endIndex = 4;

                        // Move to player inventory instead
                        if (slotIndex < 4) {
                            startIndex = 4;

                            if (grid.getType() == GridType.PATTERN) {
                                startIndex += 2; // Skip the pattern slots
                            }

                            endIndex = startIndex + (9 * 4);
                        }

                        if (mergeItemStack(stack, startIndex, endIndex, false)) {
                            slot.onSlotChanged();

                            detectAndSendChanges();

                            return ItemStack.EMPTY;
                        }
                    } else if ((grid.getType() == GridType.PATTERN && stack.getItem() == RSItems.PATTERN) || (grid instanceof IPortableGrid && stack.getItem() instanceof IStorageDiskProvider)) {
                        int startIndex = 4;
                        int endIndex = startIndex + 1;

                        // Move to player inventory instead
                        if (slotIndex == 4) {
                            startIndex = endIndex;
                            endIndex = startIndex + (9 * 4);
                        }

                        if (mergeItemStack(stack, startIndex, endIndex, false)) {
                            slot.onSlotChanged();

                            detectAndSendChanges();

                            // For some reason it doesn't detect when moving the disk from disk inventory to player inventory...
                            if (grid instanceof IPortableGrid && slotIndex == 4) {
                                ((IPortableGrid) grid).getDisk().setStackInSlot(0, ItemStack.EMPTY);
                            }

                            return ItemStack.EMPTY;
                        }

                        // When we shift click a storage disk in a portable grid and our inventory is full, the disk can't go in the storage!
                        if (grid instanceof PortableGrid) {
                            return ItemStack.EMPTY;
                        }
                    }

                    if (grid.getType() == GridType.FLUID) {
                        IFluidGridHandler fluidHandler = grid.getFluidHandler();

                        if (fluidHandler != null) {
                            slot.putStack(fluidHandler.onShiftClick((EntityPlayerMP) player, stack));
                        }
                    } else {
                        IItemGridHandler itemHandler = grid.getItemHandler();

                        if (itemHandler != null) {
                            slot.putStack(itemHandler.onShiftClick((EntityPlayerMP) player, stack));
                        }
                    }

                    detectAndSendChanges();
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    protected boolean isHeldItemDisabled() {
        // Here we check for the concrete portable grid type, not IPortableGrid, because we can move the held item in the tile
        return grid instanceof WirelessGrid || grid instanceof PortableGrid;
    }
}
