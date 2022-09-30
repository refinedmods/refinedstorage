package com.refinedmods.refinedstorage.blockentity.grid.portable;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.IGridTab;
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.StorageType;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.PortableFluidGridHandler;
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.PortableItemGridHandler;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.PortableFluidStorageCache;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.PortableItemStorageCache;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.PortableFluidGridStorageCacheListener;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.PortableItemGridStorageCacheListener;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.PortableFluidStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.PortableItemStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.tracker.FluidStorageTracker;
import com.refinedmods.refinedstorage.apiimpl.storage.tracker.ItemStorageTracker;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.FilterItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.StorageDiskItemValidator;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.WirelessGridItem;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.refinedmods.refinedstorage.network.grid.PortableGridSettingsUpdateMessage;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PortableGrid implements IGrid, IPortableGrid, IStorageDiskContainerContext {
    static final String NBT_ITEM_STORAGE_TRACKER_ID = "ItemStorageTrackerId";
    static final String NBT_FLUID_STORAGE_TRACKER_ID = "FluidStorageTrackerId";
    private final PortableItemGridHandler itemHandler = new PortableItemGridHandler(this, this);
    private final PortableFluidGridHandler fluidHandler = new PortableFluidGridHandler(this);
    private final PlayerSlot slot;
    private final List<IFilter> filters = new ArrayList<>();
    private final List<IGridTab> tabs = new ArrayList<>();
    @Nullable
    private IStorageDisk storage;
    @Nullable
    private IStorageCache cache;
    @Nullable
    private Player player;
    private ItemStack stack;
    private final FilterItemHandler filter = (FilterItemHandler) new FilterItemHandler(filters, tabs)
        .addListener((handler, slot, reading) -> {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundTag());
            }

            StackUtils.writeItems(handler, 0, stack.getTag());
        });
    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;
    private ItemStorageTracker itemStorageTracker;
    private UUID itemStorageTrackerId;
    private FluidStorageTracker fluidStorageTracker;
    private UUID fluidStorageTrackerId;

    public PortableGrid(@Nullable Player player, ItemStack stack, PlayerSlot slot) {
        this.player = player;
        this.stack = stack;
        this.slot = slot;

        this.sortingType = WirelessGridItem.getSortingType(stack);
        this.sortingDirection = WirelessGridItem.getSortingDirection(stack);
        this.searchBoxMode = WirelessGridItem.getSearchBoxMode(stack);
        this.tabSelected = WirelessGridItem.getTabSelected(stack);
        this.tabPage = WirelessGridItem.getTabPage(stack);
        this.size = WirelessGridItem.getSize(stack);

        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
        if (player != null) { //baked model does not need a storage tracker
            if (stack.getTag().contains(NBT_ITEM_STORAGE_TRACKER_ID)) {
                itemStorageTrackerId = stack.getTag().getUUID(NBT_ITEM_STORAGE_TRACKER_ID);
            } else {
                UUID id = UUID.randomUUID();
                stack.getTag().putUUID(NBT_ITEM_STORAGE_TRACKER_ID, id);
                itemStorageTrackerId = id;
            }

            if (stack.getTag().contains(NBT_FLUID_STORAGE_TRACKER_ID)) {
                fluidStorageTrackerId = stack.getTag().getUUID(NBT_FLUID_STORAGE_TRACKER_ID);
            } else {
                UUID id = UUID.randomUUID();
                stack.getTag().putUUID(NBT_FLUID_STORAGE_TRACKER_ID, id);
                fluidStorageTrackerId = id;
            }
        }


        StackUtils.readItems(disk, 4, stack.getTag());
        StackUtils.readItems(filter, 0, stack.getTag());
    }

    public void onOpen() {
        drainEnergy(RS.SERVER_CONFIG.getPortableGrid().getOpenUsage());
    }    private final BaseItemHandler disk = new BaseItemHandler(1)
        .addValidator(new StorageDiskItemValidator())
        .addListener(((handler, slot, reading) -> {
            if (player != null && !player.level.isClientSide) {
                ItemStack diskStack = handler.getStackInSlot(slot);

                if (diskStack.isEmpty()) {
                    storage = null;
                    cache = null;
                } else {
                    IStorageDisk diskInSlot = API.instance().getStorageDiskManager((ServerLevel) player.level).getByStack(getDiskInventory().getStackInSlot(0));

                    if (diskInSlot != null) {
                        StorageType type = ((IStorageDiskProvider) getDiskInventory().getStackInSlot(0).getItem()).getType();

                        if (type == StorageType.ITEM) {
                            storage = new PortableItemStorageDisk(diskInSlot, PortableGrid.this);
                            cache = new PortableItemStorageCache(PortableGrid.this);
                        } else if (type == StorageType.FLUID) {
                            storage = new PortableFluidStorageDisk(diskInSlot, PortableGrid.this);
                            cache = new PortableFluidStorageCache(PortableGrid.this);
                        }

                        storage.setSettings(null, PortableGrid.this);
                    } else {
                        storage = null;
                        cache = null;
                    }
                }

                if (cache != null) {
                    cache.invalidate(InvalidateCause.DISK_INVENTORY_CHANGED);
                }

                StackUtils.writeItems(handler, 4, stack.getTag());
            }
        }));

    public ItemStack getStack() {
        return stack;
    }

    @Override
    @Nullable
    public IStorageCache getCache() {
        return cache;
    }

    @Override
    @Nullable
    public IStorageDisk getStorage() {
        return storage;
    }

    @Override
    public void drainEnergy(int energy) {
        if (RS.SERVER_CONFIG.getPortableGrid().getUseEnergy() && ((PortableGridBlockItem) stack.getItem()).getType() != PortableGridBlockItem.Type.CREATIVE) {
            stack.getCapability(CapabilityEnergy.ENERGY, null)
                .ifPresent(energyStorage -> energyStorage.extractEnergy(energy, false));
        }
    }

    @Override
    public int getEnergy() {
        if (RS.SERVER_CONFIG.getPortableGrid().getUseEnergy() && ((PortableGridBlockItem) stack.getItem()).getType() != PortableGridBlockItem.Type.CREATIVE) {
            return stack.getCapability(CapabilityEnergy.ENERGY, null)
                .map(IEnergyStorage::getEnergyStored)
                .orElse(RS.SERVER_CONFIG.getPortableGrid().getCapacity());
        }

        return RS.SERVER_CONFIG.getPortableGrid().getCapacity();
    }

    @Override
    public BaseItemHandler getDiskInventory() {
        return disk;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public GridType getGridType() {
        return (getDiskInventory().getStackInSlot(0).isEmpty() || ((IStorageDiskProvider) getDiskInventory().getStackInSlot(0).getItem()).getType() == StorageType.ITEM) ? GridType.NORMAL : GridType.FLUID;
    }

    @Nullable
    @Override
    public IStorageCache getStorageCache() {
        return storage != null ? cache : null;
    }

    @Override
    public IStorageCacheListener createListener(ServerPlayer player) {
        return getGridType() == GridType.FLUID ? new PortableFluidGridStorageCacheListener(this, player) : new PortableItemGridStorageCacheListener(this, player);
    }

    @Nullable
    @Override
    public IItemGridHandler getItemHandler() {
        return itemHandler;
    }

    @Nullable
    @Override
    public IFluidGridHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public void addCraftingListener(ICraftingGridListener listener) {
        // NO OP
    }

    @Override
    public void removeCraftingListener(ICraftingGridListener listener) {
        // NO OP
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.refinedstorage.portable_grid");
    }

    @Override
    public int getViewType() {
        return -1;
    }

    @Override
    public int getSortingType() {
        return sortingType;
    }

    @Override
    public int getSortingDirection() {
        return sortingDirection;
    }

    @Override
    public int getSearchBoxMode() {
        return searchBoxMode;
    }

    @Override
    public int getTabSelected() {
        return tabSelected;
    }

    @Override
    public int getTabPage() {
        return Math.min(tabPage, getTotalTabPages());
    }

    @Override
    public int getTotalTabPages() {
        return (int) Math.floor((float) Math.max(0, tabs.size() - 1) / (float) IGrid.TABS_PER_PAGE);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void onViewTypeChanged(int type) {
        // NO OP
    }

    @Override
    public void onSortingTypeChanged(int type) {
        RS.NETWORK_HANDLER.sendToServer(new PortableGridSettingsUpdateMessage(getViewType(), getSortingDirection(), type, getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingType = type;

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RS.NETWORK_HANDLER.sendToServer(new PortableGridSettingsUpdateMessage(getViewType(), direction, getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingDirection = direction;

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RS.NETWORK_HANDLER.sendToServer(new PortableGridSettingsUpdateMessage(getViewType(), getSortingDirection(), getSortingType(), searchBoxMode, getSize(), getTabSelected(), getTabPage()));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void onSizeChanged(int size) {
        RS.NETWORK_HANDLER.sendToServer(new PortableGridSettingsUpdateMessage(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), size, getTabSelected(), getTabPage()));

        this.size = size;

        BaseScreen.executeLater(GridScreen.class, BaseScreen::init);
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        this.tabSelected = tab == tabSelected ? -1 : tab;

        RS.NETWORK_HANDLER.sendToServer(new PortableGridSettingsUpdateMessage(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), tabSelected, getTabPage()));

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0 && page <= getTotalTabPages()) {
            RS.NETWORK_HANDLER.sendToServer(new PortableGridSettingsUpdateMessage(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), page));

            this.tabPage = page;
        }
    }

    @Override
    public List<IFilter> getFilters() {
        return filters;
    }

    @Override
    public List<IGridTab> getTabs() {
        return tabs;
    }

    @Override
    public IItemHandlerModifiable getFilter() {
        return filter;
    }

    @Override
    public ItemStorageTracker getItemStorageTracker() {
        if (itemStorageTracker == null) {
            if (player != null) {
                if (itemStorageTrackerId == null) {
                    this.itemStorageTrackerId = UUID.randomUUID();
                }

                this.itemStorageTracker = (ItemStorageTracker) API.instance().getStorageTrackerManager((ServerLevel) player.level).getOrCreate(itemStorageTrackerId, StorageType.ITEM);
            }
        }

        return itemStorageTracker;
    }

    @Override
    public FluidStorageTracker getFluidStorageTracker() {
        if (fluidStorageTracker == null) {
            if (player != null) {
                if (fluidStorageTrackerId == null) {
                    this.fluidStorageTrackerId = UUID.randomUUID();
                }

                this.fluidStorageTracker = (FluidStorageTracker) API.instance().getStorageTrackerManager((ServerLevel) player.level).getOrCreate(fluidStorageTrackerId, StorageType.FLUID);
            }
        }

        return fluidStorageTracker;
    }

    @Override
    public CraftingContainer getCraftingMatrix() {
        return null;
    }

    @Override
    public ResultContainer getCraftingResult() {
        return null;
    }

    @Override
    public void onCraftingMatrixChanged() {
        // NO OP
    }

    @Override
    public void onCrafted(Player player, @Nullable IStackList<ItemStack> availableItems, @Nullable IStackList<ItemStack> usedItems) {
        // NO OP
    }

    @Override
    public void onClear(Player player) {
        // NO OP
    }

    @Override
    public void onCraftedShift(Player player) {
        // NO OP
    }

    @Override
    public void onRecipeTransfer(Player player, ItemStack[][] recipe) {
        // NO OP
    }

    @Override
    public void onClosed(Player player) {
        if (!player.getCommandSenderWorld().isClientSide) {
            StackUtils.writeItems(disk, 4, stack.getTag());
        }
    }

    private boolean hasDisk() {
        return !disk.getStackInSlot(0).isEmpty();
    }

    @Override
    public boolean isGridActive() {
        if (RS.SERVER_CONFIG.getPortableGrid().getUseEnergy() &&
            ((PortableGridBlockItem) stack.getItem()).getType() != PortableGridBlockItem.Type.CREATIVE &&
            stack.getCapability(CapabilityEnergy.ENERGY).orElse(null).getEnergyStored() <= RS.SERVER_CONFIG.getPortableGrid().getOpenUsage()) {
            return false;
        }

        return hasDisk();
    }

    @Override
    public int getSlotId() {
        return slot.getSlotIdInPlayerInventory();
    }

    @Nullable
    private UUID getDiskId() {
        return !hasDisk() ? null : ((IStorageDiskProvider) disk.getStackInSlot(0).getItem()).getId(disk.getStackInSlot(0));
    }

    private int getStored() {
        API.instance().getStorageDiskSync().sendRequest(getDiskId());

        StorageDiskSyncData data = API.instance().getStorageDiskSync().getData(getDiskId());

        return data == null ? 0 : data.getStored();
    }

    private int getCapacity() {
        API.instance().getStorageDiskSync().sendRequest(getDiskId());

        StorageDiskSyncData data = API.instance().getStorageDiskSync().getData(getDiskId());

        return data == null ? 0 : data.getCapacity();
    }

    @Override
    public PortableGridDiskState getDiskState() {
        if (!hasDisk()) {
            return PortableGridDiskState.NONE;
        }

        if (!isGridActive()) {
            return PortableGridDiskState.DISCONNECTED;
        }

        int stored = getStored();
        int capacity = getCapacity();

        if (stored == capacity) {
            return PortableGridDiskState.FULL;
        } else if ((int) ((float) stored / (float) capacity * 100F) >= DiskState.DISK_NEAR_CAPACITY_THRESHOLD) {
            return PortableGridDiskState.NEAR_CAPACITY;
        } else {
            return PortableGridDiskState.NORMAL;
        }
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.INSERT_EXTRACT;
    }




}
