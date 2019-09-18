package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridCraftingListener;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.grid.CraftingGridSlot;
import com.raoulvdberge.refinedstorage.container.slot.grid.ResultCraftingGridSlot;
import com.raoulvdberge.refinedstorage.container.slot.legacy.BaseLegacySlot;
import com.raoulvdberge.refinedstorage.container.slot.legacy.DisabledLegacySlot;
import com.raoulvdberge.refinedstorage.container.slot.legacy.FilterLegacySlot;
import com.raoulvdberge.refinedstorage.screen.IResizableDisplay;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class GridContainer extends BaseContainer implements IGridCraftingListener {
    private IGrid grid;
    private IStorageCache cache;
    private IStorageCacheListener listener;
    private IResizableDisplay display;

    private ResultCraftingGridSlot craftingResultSlot;
    private BaseLegacySlot patternResultSlot;

    public GridContainer(IGrid grid, IResizableDisplay display, @Nullable TileBase gridTile, PlayerEntity player, int windowId) {
        super(RSContainers.GRID, gridTile, player, windowId);

        this.grid = grid;
        this.display = display;

        initSlots();

        grid.addCraftingListener(this);
    }

    public void initSlots() {
        this.inventorySlots.clear();
        // this.inventoryItemStacks.clear(); // TODO at

        this.transferManager.clearTransfers();

        addFilterSlots();

        if (grid instanceof IPortableGrid) {
            addPortableGridSlots();
        }

        if (grid.getGridType() == GridType.CRAFTING) {
            addCraftingSlots();
        } else if (grid.getGridType() == GridType.PATTERN) {
            addPatternSlots();
        }

        transferManager.setNotFoundHandler(slotIndex -> {
            if (!getPlayer().getEntityWorld().isRemote) {
                Slot slot = inventorySlots.get(slotIndex);

                if (slot.getHasStack()) {
                    if (slot == craftingResultSlot) {
                        grid.onCraftedShift(getPlayer());

                        detectAndSendChanges();
                    } else {
                        ItemStack stack = slot.getStack();

                        if (grid.getGridType() == GridType.FLUID) {
                            IFluidGridHandler fluidHandler = grid.getFluidHandler();

                            if (fluidHandler != null) {
                                slot.putStack(fluidHandler.onShiftClick((ServerPlayerEntity) getPlayer(), stack));
                            }
                        } else {
                            IItemGridHandler itemHandler = grid.getItemHandler();

                            if (itemHandler != null) {
                                slot.putStack(itemHandler.onShiftClick((ServerPlayerEntity) getPlayer(), stack));
                            } else if (slot instanceof CraftingGridSlot && mergeItemStack(stack, 14, 14 + (9 * 4), false)) {
                                slot.onSlotChanged();

                                // This is needed because when a grid is disconnected,
                                // and a player shift clicks from the matrix to the inventory (this if case),
                                // the crafting inventory isn't being notified.
                                grid.onCraftingMatrixChanged();
                            }
                        }

                        detectAndSendChanges();
                    }
                }
            }

            return ItemStack.EMPTY;
        });

        addPlayerInventory(8, display.getYPlayerInventory());
    }

    private void addPortableGridSlots() {
        addSlot(new SlotItemHandler(((IPortableGrid) grid).getDisk(), 0, 204, 6));

        transferManager.addBiTransfer(getPlayer().inventory, ((IPortableGrid) grid).getDisk());
    }

    private void addFilterSlots() {
        int yStart = 6;

        if (grid instanceof IPortableGrid) {
            yStart = 38;
        }

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(grid.getFilter(), i, 204, yStart + (18 * i)));
        }

        transferManager.addBiTransfer(getPlayer().inventory, grid.getFilter());
    }

    private void addCraftingSlots() {
        int headerAndSlots = display.getTopHeight() + (display.getVisibleRows() * 18);

        int x = 26;
        int y = headerAndSlots + 4;

        for (int i = 0; i < 9; ++i) {
            addSlot(new CraftingGridSlot(grid.getCraftingMatrix(), i, x, y));

            x += 18;

            if ((i + 1) % 3 == 0) {
                y += 18;
                x = 26;
            }
        }

        addSlot(craftingResultSlot = new ResultCraftingGridSlot(getPlayer(), grid, 0, 130 + 4, headerAndSlots + 22));
    }

    private void addPatternSlots() {
        int headerAndSlots = display.getTopHeight() + (display.getVisibleRows() * 18);

        addSlot(new SlotItemHandler(((NetworkNodeGrid) grid).getPatterns(), 0, 172, headerAndSlots + 4));
        addSlot(new SlotItemHandler(((NetworkNodeGrid) grid).getPatterns(), 1, 172, headerAndSlots + 40));

        transferManager.addBiTransfer(getPlayer().inventory, ((NetworkNodeGrid) grid).getPatterns());

        // Processing patterns
        int ox = 8;
        int x = ox;
        int y = headerAndSlots + 4;

        for (int i = 0; i < 9 * 2; ++i) {
            addSlot(new FilterSlot(((NetworkNodeGrid) grid).getProcessingMatrix(), i, x, y, FilterSlot.FILTER_ALLOW_SIZE).setEnableHandler(() -> ((NetworkNodeGrid) grid).isProcessingPattern() && ((NetworkNodeGrid) grid).getType() == IType.ITEMS));
            addSlot(new FluidFilterSlot(((NetworkNodeGrid) grid).getProcessingMatrixFluids(), i, x, y, FilterSlot.FILTER_ALLOW_SIZE).setEnableHandler(() -> ((NetworkNodeGrid) grid).isProcessingPattern() && ((NetworkNodeGrid) grid).getType() == IType.FLUIDS));

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
            addSlot(new FilterLegacySlot(grid.getCraftingMatrix(), i, x, y).setEnableHandler(() -> !((NetworkNodeGrid) grid).isProcessingPattern()));

            x += 18;

            if ((i + 1) % 3 == 0) {
                y += 18;
                x = 26;
            }
        }

        addSlot(patternResultSlot = (new DisabledLegacySlot(grid.getCraftingResult(), 0, 134, headerAndSlots + 22).setEnableHandler(() -> !((NetworkNodeGrid) grid).isProcessingPattern())));
    }

    public IGrid getGrid() {
        return grid;
    }

    @Override
    public void onCraftingMatrixChanged() {
        for (int i = 0; i < inventorySlots.size(); ++i) {
            Slot slot = inventorySlots.get(i);

            if (slot instanceof CraftingGridSlot || slot == craftingResultSlot || slot == patternResultSlot) {
                for (IContainerListener listener : new ArrayList<IContainerListener>()) { // TODO AT for listeners
                    // @Volatile: We can't use IContainerListener#sendSlotContents since ServerPlayerEntity blocks SlotCrafting changes...
                    if (listener instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) listener).connection.sendPacket(new SSetSlotPacket(windowId, i, slot.getStack()));
                    }
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
                listener = grid.createListener((ServerPlayerEntity) getPlayer());
                cache = grid.getStorageCache();

                cache.addListener(listener);
            }
        }

        super.detectAndSendChanges();
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);

        if (!player.getEntityWorld().isRemote) {
            grid.onClosed(player);

            if (cache != null && listener != null) {
                cache.removeListener(listener);
            }
        }

        grid.removeCraftingListener(this);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        if (slot == craftingResultSlot || slot == patternResultSlot) {
            return false;
        }

        return super.canMergeSlot(stack, slot);
    }

    @Override
    protected boolean isHeldItemDisabled() {
        // Here we check for the concrete portable grid type, not IPortableGrid, because we *CAN* move the held item in the portable grid tile
        return grid instanceof WirelessGrid || grid instanceof PortableGrid;
    }
}
