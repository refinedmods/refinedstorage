package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.grid.portable.IPortableGrid;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.container.slot.grid.CraftingGridSlot;
import com.refinedmods.refinedstorage.container.slot.grid.ResultCraftingGridSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyBaseSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyDisabledSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot;
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GridContainerMenu extends BaseContainerMenu implements ICraftingGridListener {
    private final IGrid grid;
    private IStorageCache storageCache;
    private IStorageCacheListener storageCacheListener;
    private IScreenInfoProvider screenInfoProvider;

    private ResultCraftingGridSlot craftingResultSlot;
    private LegacyBaseSlot patternResultSlot;
    private List<Slot> itemPatternSlots = new ArrayList<>();
    private List<Slot> fluidPatternSlots = new ArrayList<>();
    private int patternScrollOffset;

    public GridContainerMenu(IGrid grid, @Nullable BaseBlockEntity blockEntity, Player player, int windowId) {
        super(RSContainerMenus.GRID.get(), blockEntity, player, windowId);

        this.grid = grid;

        grid.addCraftingListener(this);
    }

    public IScreenInfoProvider getScreenInfoProvider() {
        return screenInfoProvider;
    }

    public void setScreenInfoProvider(IScreenInfoProvider screenInfoProvider) {
        this.screenInfoProvider = screenInfoProvider;
    }

    public void initSlots() {
        this.slots.clear();
        this.lastSlots.clear();

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
            if (!getPlayer().getCommandSenderWorld().isClientSide) {
                Slot slot = slots.get(slotIndex);
                if (grid instanceof IPortableGrid && slot instanceof SlotItemHandler && ((SlotItemHandler) slot).getItemHandler().equals(((IPortableGrid) grid).getDiskInventory())) {
                    return ItemStack.EMPTY;
                }

                if (slot.hasItem()) {
                    if (slot == craftingResultSlot) {
                        grid.onCraftedShift(getPlayer());

                        broadcastChanges();
                    } else {
                        ItemStack stack = slot.getItem();

                        if (grid.getGridType() == GridType.FLUID) {
                            IFluidGridHandler fluidHandler = grid.getFluidHandler();

                            if (fluidHandler != null) {
                                slot.set(fluidHandler.onInsert((ServerPlayer) getPlayer(), stack));
                            }
                        } else {
                            IItemGridHandler itemHandler = grid.getItemHandler();

                            if (itemHandler != null) {
                                slot.set(itemHandler.onInsert((ServerPlayer) getPlayer(), stack, false));
                            } else if (slot instanceof CraftingGridSlot && moveItemStackTo(stack, 14, 14 + (9 * 4), false)) {
                                slot.setChanged();

                                // This is needed because when a grid is disconnected,
                                // and a player shift clicks from the matrix to the inventory (this if case),
                                // the crafting inventory isn't being notified.
                                grid.onCraftingMatrixChanged();
                            }
                        }

                        broadcastChanges();
                    }
                }
            }

            return ItemStack.EMPTY;
        });

        addPlayerInventory(8, screenInfoProvider.getYPlayerInventory());
    }

    private void addPortableGridSlots() {
        addSlot(new SlotItemHandler(((IPortableGrid) grid).getDiskInventory(), 0, 204, 6));

        transferManager.addBiTransfer(getPlayer().getInventory(), ((IPortableGrid) grid).getDiskInventory());
    }

    private void addFilterSlots() {
        int yStart = 6;

        if (grid instanceof IPortableGrid) {
            yStart = 38;
        }

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(grid.getFilter(), i, 204, yStart + (18 * i)));
        }

        transferManager.addBiTransfer(getPlayer().getInventory(), grid.getFilter());
    }

    private void addCraftingSlots() {
        int headerAndSlots = screenInfoProvider.getTopHeight() + (screenInfoProvider.getVisibleRows() * 18);

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

        craftingResultSlot = new ResultCraftingGridSlot(getPlayer(), grid, 0, 130 + 4, headerAndSlots + 22);
        addSlot(craftingResultSlot);
    }

    private void addPatternSlots() {
        itemPatternSlots.clear();
        fluidPatternSlots.clear();
        int headerAndSlots = screenInfoProvider.getTopHeight() + (screenInfoProvider.getVisibleRows() * 18);

        addSlot(new SlotItemHandler(((GridNetworkNode) grid).getPatterns(), 0, 172, headerAndSlots + 4));
        addSlot(new SlotItemHandler(((GridNetworkNode) grid).getPatterns(), 1, 172, headerAndSlots + 40));

        transferManager.addBiTransfer(getPlayer().getInventory(), ((GridNetworkNode) grid).getPatterns());

        // Processing patterns
        int ox = 8;
        int x = ox;
        int y = headerAndSlots + 4;

        for (int i = 0; i < GridNetworkNode.PROCESSING_MATRIX_SIZE * 2; ++i) {
            int itemFilterSlotConfig = FilterSlot.FILTER_ALLOW_SIZE;
            if (i < GridNetworkNode.PROCESSING_MATRIX_SIZE) {
                itemFilterSlotConfig |= FilterSlot.FILTER_ALLOW_ALTERNATIVES;
            }

            int fluidFilterSlotConfig = FluidFilterSlot.FILTER_ALLOW_SIZE;
            if (i < GridNetworkNode.PROCESSING_MATRIX_SIZE) {
                fluidFilterSlotConfig |= FluidFilterSlot.FILTER_ALLOW_ALTERNATIVES;
            }

            int finalI = i;
            itemPatternSlots.add(addSlot(new FilterSlot(((GridNetworkNode) grid).getProcessingMatrix(), i, x, y, itemFilterSlotConfig)
                .setEnableHandler(() -> getSlotEnabled(finalI, true))));
            fluidPatternSlots.add(addSlot(new FluidFilterSlot(((GridNetworkNode) grid).getProcessingMatrixFluids(), i, x, y, fluidFilterSlotConfig)
                .setEnableHandler(() -> getSlotEnabled(finalI, false))));

            x += 18;

            if ((i + 1) % 3 == 0) {
                if (i == GridNetworkNode.PROCESSING_MATRIX_SIZE - 1) {
                    ox = 93;
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
            addSlot(new LegacyFilterSlot(grid.getCraftingMatrix(), i, x, y).setEnableHandler(() -> !((GridNetworkNode) grid).isProcessingPattern()));

            x += 18;

            if ((i + 1) % 3 == 0) {
                y += 18;
                x = 26;
            }
        }

        patternResultSlot = new LegacyDisabledSlot(grid.getCraftingResult(), 0, 134, headerAndSlots + 22).setEnableHandler(() -> !((GridNetworkNode) grid).isProcessingPattern());
        addSlot(patternResultSlot);
    }

    private boolean getSlotEnabled(int i, boolean item) {
        if (!((GridNetworkNode) grid).isProcessingPattern() || !isVisible(i)) {
            return false;
        }

        if (item) {
            if (itemPatternSlots.get(i).hasItem()) {
                return true;
            }

            if (((FluidFilterSlot) fluidPatternSlots.get(i)).hasStack()) {
                return false;
            }

            return ((GridNetworkNode) grid).getType() == IType.ITEMS;
        } else {
            if (((FluidFilterSlot) fluidPatternSlots.get(i)).hasStack()) {
                return true;
            }

            if (itemPatternSlots.get(i).hasItem()) {
                return false;
            }

            return ((GridNetworkNode) grid).getType() == IType.FLUIDS;
        }
    }

    private boolean isVisible(int slotNumber) {
        return (slotNumber >= patternScrollOffset * 3
            && slotNumber < patternScrollOffset * 3 + 9)

            || (slotNumber >= patternScrollOffset * 3 + GridNetworkNode.PROCESSING_MATRIX_SIZE
            && slotNumber < patternScrollOffset * 3 + GridNetworkNode.PROCESSING_MATRIX_SIZE + 9);
    }

    public IGrid getGrid() {
        return grid;
    }

    @Override
    public void onCraftingMatrixChanged() {
        for (int i = 0; i < slots.size(); ++i) {
            Slot slot = slots.get(i);

            if (slot instanceof CraftingGridSlot || slot == craftingResultSlot || slot == patternResultSlot) {
                for (ContainerListener listener : containerListeners) {
                    // @Volatile: We can't use ContainerListener#slotChanged since ServerPlayer blocks ResultSlot changes...
                    if (listener instanceof ServerPlayer) {
                        ((ServerPlayer) listener).connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), i, slot.getItem()));
                    }
                }
            }
        }
    }

    @Override
    public void broadcastChanges() {
        if (!getPlayer().level().isClientSide) {
            // The grid is offline.
            if (grid.getStorageCache() == null) {
                // The grid just went offline, there is still a listener.
                if (storageCacheListener != null) {
                    // Remove it from the previous cache and clean up.
                    storageCache.removeListener(storageCacheListener);

                    storageCacheListener = null;
                    storageCache = null;
                }
            } else if (storageCacheListener == null) { // The grid came online.
                storageCacheListener = grid.createListener((ServerPlayer) getPlayer());
                storageCache = grid.getStorageCache();

                storageCache.addListener(storageCacheListener);
            }
        }

        super.broadcastChanges();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!player.getCommandSenderWorld().isClientSide) {
            grid.onClosed(player);

            if (storageCache != null && storageCacheListener != null) {
                storageCache.removeListener(storageCacheListener);
            }
        }

        grid.removeCraftingListener(this);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if (slot == craftingResultSlot || slot == patternResultSlot) {
            return false;
        }

        return super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public int getDisabledSlotNumber() {
        return grid.getSlotId();
    }

    public void updatePatternSlotPositions(int newOffset) {
        patternScrollOffset = newOffset;
        int yPosition = screenInfoProvider.getTopHeight() + (screenInfoProvider.getVisibleRows() * 18) + 4;
        int originalYPosition = yPosition;

        for (int i = 0; i < itemPatternSlots.size(); i++) {

            if (i == GridNetworkNode.PROCESSING_MATRIX_SIZE) { // reset when reaching output slots
                yPosition = originalYPosition;
            }

            if (isVisible(i)) {
                itemPatternSlots.get(i).y = yPosition;
                fluidPatternSlots.get(i).y = yPosition;
                if ((i + 1) % 3 == 0) {
                    yPosition += 18;
                }
            }
        }
    }
}