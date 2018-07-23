package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.slot.*;
import com.raoulvdberge.refinedstorage.gui.IResizableDisplay;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class ContainerGrid extends ContainerBase {
    private IGrid grid;
    private IStorageCache cache;
    private IStorageCacheListener listener;
    private IResizableDisplay display;

    private SlotGridCraftingResult craftingResultSlot;
    private SlotDisabled patternResultSlot;

    public ContainerGrid(IGrid grid, IResizableDisplay display, @Nullable TileBase gridTile, EntityPlayer player) {
        super(gridTile, player);

        this.grid = grid;
        this.display = display;

        initSlots();
    }

    public void initSlots() {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();

        int headerAndSlots = getTabDelta() + display.getTopHeight() + (display.getVisibleRows() * 18);

        if (grid.getGridType() != GridType.FLUID) {
            int yStart = 6;

            if (grid instanceof IPortableGrid) {
                yStart = 38;
            }

            for (int i = 0; i < 4; ++i) {
                addSlotToContainer(new SlotItemHandler(grid.getFilter(), i, 204, yStart + (18 * i) + getTabDelta()));
            }
        }

        if (grid.getGridType() == GridType.PATTERN) {
            addSlotToContainer(new SlotItemHandler(((NetworkNodeGrid) grid).getPatterns(), 0, 172, headerAndSlots + 4));
            addSlotToContainer(new SlotItemHandler(((NetworkNodeGrid) grid).getPatterns(), 1, 172, headerAndSlots + 40));
        }

        if (grid instanceof IPortableGrid) {
            addSlotToContainer(new SlotItemHandler(((IPortableGrid) grid).getDisk(), 0, 204, 6 + getTabDelta()));
        }

        addPlayerInventory(8, display.getYPlayerInventory());

        if (grid.getGridType() == GridType.CRAFTING) {
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

            addSlotToContainer(craftingResultSlot = new SlotGridCraftingResult(this, getPlayer(), grid, 0, 130 + 4, headerAndSlots + 22));
        } else if (grid.getGridType() == GridType.PATTERN) {
            // Processing patterns
            int ox = 8;
            int x = ox;
            int y = headerAndSlots + 4;

            for (int i = 0; i < 9 * 2; ++i) {
                addSlotToContainer(new SlotFilterItemOrFluid((NetworkNodeGrid) grid, i, x, y, SlotFilter.FILTER_ALLOW_SIZE, (slot, amount) -> {
                    if (amount > 0 && amount <= Fluid.BUCKET_VOLUME && slot < ((NetworkNodeGrid) grid).getMatrixProcessingFluids().getSlots()) {
                        ((NetworkNodeGrid) grid).getMatrixProcessingFluids().getStackInSlot(slot).setCount(amount);
                    }
                }, Fluid.BUCKET_VOLUME, () -> ((NetworkNodeGrid) grid).isProcessingPattern()));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    if (i == 8) {
                        ox = 98;
                        x = ox;
                        y = headerAndSlots + 4;
                    } else {
                        x = ox;
                        y += 18;
                    }
                }
            }

            // Regular patterns
            x = 26;
            y = headerAndSlots + 4;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotFilterLegacy(grid.getCraftingMatrix(), i, x, y, () -> !((NetworkNodeGrid) grid).isProcessingPattern()));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 26;
                }
            }

            addSlotToContainer(patternResultSlot = new SlotDisabled(grid.getCraftingResult(), 0, 134, headerAndSlots + 22, () -> !((NetworkNodeGrid) grid).isProcessingPattern()));
        }
    }

    private int getTabDelta() {
        return !grid.getTabs().isEmpty() ? IGridTab.TAB_HEIGHT - 4 : 0;
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
    public void detectAndSendChanges() {
        if (!getPlayer().world.isRemote) {
            // The grid is offline.
            if (grid.getStorageCache() == null) {
                // The grid just went offline, there is still a listener.
                if (listener != null) {
                    // Remove it from the previous cache and clean up.
                    cache.removeListener(listener);

                    listener = null;
                    cache = null;
                }
            } else if (listener == null) { // The grid came online.
                listener = grid.createListener((EntityPlayerMP) getPlayer());
                cache = grid.getStorageCache();

                cache.addListener(listener);
            }
        }

        super.detectAndSendChanges();
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        if (!player.getEntityWorld().isRemote) {
            grid.onClosed(player);

            if (cache != null && listener != null) {
                cache.removeListener(listener);
            }
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
                if (grid instanceof IPortableGrid && slot.slotNumber == 4) { // Prevent moving disk slot into portable grid itself
                    return ItemStack.EMPTY;
                } else if (grid.getGridType() == GridType.PATTERN && slot.slotNumber == 5) { // From output slot to inventory
                    ItemStack stack = slot.getStack();

                    int startIndex = 5;
                    int endIndex = startIndex + (9 * 4);

                    if (mergeItemStack(stack, startIndex, endIndex, false)) {
                        slot.onSlotChanged();

                        detectAndSendChanges();
                    }

                    return ItemStack.EMPTY;
                } else if (slot == craftingResultSlot) {
                    grid.onCraftedShift(player);

                    sendCraftingSlots();
                    detectAndSendChanges();
                } else if (slot != patternResultSlot && !(slot instanceof SlotFilterLegacy)) {
                    ItemStack stack = slot.getStack();

                    if (grid.getGridType() != GridType.FLUID && stack.getItem() == RSItems.FILTER) {
                        int startIndex = 0;
                        int endIndex = 4;

                        // Move to player inventory instead
                        if (slotIndex < 4) {
                            startIndex = 4;

                            if (grid.getGridType() == GridType.PATTERN) {
                                startIndex += 2; // Skip the pattern slots
                            }

                            endIndex = startIndex + (9 * 4);
                        }

                        if (mergeItemStack(stack, startIndex, endIndex, false)) {
                            slot.onSlotChanged();

                            detectAndSendChanges();

                            // For some reason it doesn't detect when moving the filter from filter inventory to player inventory...
                            if (slotIndex < 4) {
                                grid.getFilter().setStackInSlot(slotIndex, ItemStack.EMPTY);
                            }

                            return ItemStack.EMPTY;
                        }
                    } else if ((grid.getGridType() == GridType.PATTERN && stack.getItem() == RSItems.PATTERN) || (grid instanceof IPortableGrid && stack.getItem() instanceof IStorageDiskFactory)) {
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

                    if (grid.getGridType() == GridType.FLUID) {
                        IFluidGridHandler fluidHandler = grid.getFluidHandler();

                        if (fluidHandler != null) {
                            slot.putStack(fluidHandler.onShiftClick((EntityPlayerMP) player, stack));
                        }
                    } else {
                        IItemGridHandler itemHandler = grid.getItemHandler();

                        if (itemHandler != null) {
                            slot.putStack(itemHandler.onShiftClick((EntityPlayerMP) player, stack));
                        } else if (slot instanceof SlotGridCrafting && mergeItemStack(stack, 4, 4 + (9 * 4), false)) {
                            slot.onSlotChanged();
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
        // Here we check for the concrete portable grid type, not IPortableGrid, because we *CAN* move the held item in the portable grid tile
        return grid instanceof WirelessGrid || grid instanceof PortableGrid;
    }
}
