package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridCraftingListener;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.StorageType;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.storage.tracker.IStorageTracker;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.FluidGridHandlerPortable;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.ItemGridHandlerPortable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.DiskDriveNetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.PortableFluidStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.PortableItemStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.listener.PortableFluidGridStorageCacheListener;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.listener.PortableItemGridStorageCacheListener;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.PortableFluidStorageDisk;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.PortableItemStorageDisk;
import com.raoulvdberge.refinedstorage.apiimpl.storage.tracker.FluidStorageTracker;
import com.raoulvdberge.refinedstorage.apiimpl.storage.tracker.ItemStorageTracker;
import com.raoulvdberge.refinedstorage.block.BlockPortableGrid;
import com.raoulvdberge.refinedstorage.block.enums.PortableGridDiskState;
import com.raoulvdberge.refinedstorage.block.enums.PortableGridType;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerTile;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.tile.BaseTile;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.tile.grid.GridTile;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TilePortableGrid extends BaseTile implements IGrid, IPortableGrid, IRedstoneConfigurable, IStorageDiskContainerContext, IPortableGrid.IPortableGridRenderInfo {
    public static int FACTORY_ID;

    public static final TileDataParameter<Integer, TilePortableGrid> REDSTONE_MODE = RedstoneMode.createParameter();
    private static final TileDataParameter<Integer, TilePortableGrid> ENERGY_STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.energyStorage.getEnergyStored());
    private static final TileDataParameter<Integer, TilePortableGrid> SORTING_DIRECTION = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSortingDirection, (t, v) -> {
        if (IGrid.isValidSortingDirection(v)) {
            t.setSortingDirection(v);
            t.markDirty();
        }
    }, (initial, p) -> GridTile.trySortGrid(initial));
    private static final TileDataParameter<Integer, TilePortableGrid> SORTING_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSortingType, (t, v) -> {
        if (IGrid.isValidSortingType(v)) {
            t.setSortingType(v);
            t.markDirty();
        }
    }, (initial, p) -> GridTile.trySortGrid(initial));
    private static final TileDataParameter<Integer, TilePortableGrid> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSearchBoxMode, (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.setSearchBoxMode(v);
            t.markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getSearchField().setMode(p)));
    private static final TileDataParameter<Integer, TilePortableGrid> SIZE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSize, (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.setSize(v);
            t.markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));
    private static final TileDataParameter<Integer, TilePortableGrid> TAB_SELECTED = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getTabSelected, (t, v) -> {
        t.setTabSelected(v == t.getTabSelected() ? -1 : v);
        t.markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort()));
    private static final TileDataParameter<Integer, TilePortableGrid> TAB_PAGE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getTabPage, (t, v) -> {
        if (v >= 0 && v <= t.getTotalTabPages()) {
            t.setTabPage(v);
            t.markDirty();
        }
    });

    private static final String NBT_ENERGY = "Energy";
    private static final String NBT_DISK_STATE = "DiskState";
    private static final String NBT_CONNECTED = "Connected";
    private static final String NBT_STORAGE_TRACKER = "StorageTracker";
    private static final String NBT_FLUID_STORAGE_TRACKER = "FluidStorageTracker";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_ENCHANTMENTS = "ench"; // @Volatile: minecraft specific nbt key
    private EnergyStorage energyStorage = recreateEnergyStorage(0);
    private LazyOptional<EnergyStorage> energyStorageCap = LazyOptional.of(() -> energyStorage);
    private PortableGridType type;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;

    private GridType clientGridType;

    private List<IFilter> filters = new ArrayList<>();
    private List<IGridTab> tabs = new ArrayList<>();
    private ItemHandlerFilter filter = new ItemHandlerFilter(filters, tabs, new ListenerTile(this));
    private ItemHandlerBase disk = new ItemHandlerBase(1, new ListenerTile(this), DiskDriveNetworkNode.VALIDATOR_STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (world != null && !world.isRemote) {
                loadStorage();
            }
        }
    };

    @Nullable
    private IStorageDisk storage;
    @Nullable
    private IStorageCache cache;

    private ItemGridHandlerPortable itemHandler = new ItemGridHandlerPortable(this, this);
    private FluidGridHandlerPortable fluidHandler = new FluidGridHandlerPortable(this);
    private PortableGridDiskState diskState = PortableGridDiskState.NONE;
    private boolean connected;

    private ItemStorageTracker storageTracker = new ItemStorageTracker(this::markDirty);
    private FluidStorageTracker fluidStorageTracker = new FluidStorageTracker(this::markDirty);
    private ListNBT enchants = null;

    public TilePortableGrid() {
        super(RSTiles.PORTABLE_GRID);

        dataManager.addWatchedParameter(REDSTONE_MODE);
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
        dataManager.addWatchedParameter(SIZE);
        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(TAB_PAGE);
    }

    private void loadStorage() {
        ItemStack diskStack = getDisk().getStackInSlot(0);

        if (diskStack.isEmpty()) {
            this.storage = null;
            this.cache = null;
        } else {
            IStorageDisk disk = API.instance().getStorageDiskManager((ServerWorld) world).getByStack(getDisk().getStackInSlot(0));

            if (disk != null) {
                StorageType type = ((IStorageDiskProvider) getDisk().getStackInSlot(0).getItem()).getType();

                switch (type) {
                    case ITEM:
                        this.storage = new PortableItemStorageDisk(disk, this);
                        this.cache = new PortableItemStorageCache(this);
                        break;
                    case FLUID:
                        this.storage = new PortableFluidStorageDisk(disk, this);
                        this.cache = new PortableFluidStorageCache(this);
                        break;
                }

                this.storage.setSettings(TilePortableGrid.this::checkIfDiskStateChanged, TilePortableGrid.this);
            } else {
                this.storage = null;
                this.cache = null;
            }
        }

        if (cache != null) {
            cache.invalidate();
        }

        checkIfDiskStateChanged();

        WorldUtils.updateBlock(world, pos);
    }

    public PortableGridDiskState getDiskState() {
        return diskState;
    }

    public boolean isConnected() {
        return connected;
    }

    public PortableGridType getPortableType() {
        if (type == null) {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() == RSBlocks.PORTABLE_GRID) {
                this.type = state.get(BlockPortableGrid.TYPE);
            }
        }

        return type == null ? PortableGridType.NORMAL : type;
    }

    public void onPassItemContext(ItemStack stack) {
        /* TODO this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
        this.tabSelected = ItemWirelessGrid.getTabSelected(stack);
        this.tabPage = ItemWirelessGrid.getTabPage(stack);
        this.size = ItemWirelessGrid.getSize(stack);
*/
        IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null);

        this.energyStorage = recreateEnergyStorage(energyStorage != null ? energyStorage.getEnergyStored() : 0);

        if (stack.hasTag()) {
            for (int i = 0; i < 4; ++i) {
                StackUtils.readItems(filter, i, stack.getTag());
            }

            StackUtils.readItems(disk, 4, stack.getTag());

            this.redstoneMode = RedstoneMode.read(stack.getTag());

            if (stack.getTag().contains(PortableGrid.NBT_STORAGE_TRACKER)) {
                storageTracker.readFromNbt(stack.getTag().getList(PortableGrid.NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
            }

            if (stack.getTag().contains(PortableGrid.NBT_FLUID_STORAGE_TRACKER)) {
                fluidStorageTracker.readFromNbt(stack.getTag().getList(PortableGrid.NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
            }

            if (stack.getTag().contains(NBT_ENCHANTMENTS)) {
                enchants = stack.getTag().getList(NBT_ENCHANTMENTS, Constants.NBT.TAG_COMPOUND);
            }
        }

        this.diskState = getDiskState(this);

        markDirty();
    }

    private EnergyStorage recreateEnergyStorage(int energyStored) {
        return new EnergyStorage(RS.INSTANCE.config.portableGridCapacity, RS.INSTANCE.config.portableGridCapacity, 0, energyStored);
    }

    public ItemStack getAsItem() {
        ItemStack stack = new ItemStack(RSBlocks.PORTABLE_GRID, 1/* TODO, getPortableType() == PortableGridType.NORMAL ? ItemBlockPortableGrid.TYPE_NORMAL : ItemBlockPortableGrid.TYPE_CREATIVE*/);

        stack.setTag(new CompoundNBT());

        stack.getTag().putInt(GridNetworkNode.NBT_SORTING_DIRECTION, sortingDirection);
        stack.getTag().putInt(GridNetworkNode.NBT_SORTING_TYPE, sortingType);
        stack.getTag().putInt(GridNetworkNode.NBT_SEARCH_BOX_MODE, searchBoxMode);
        stack.getTag().putInt(GridNetworkNode.NBT_SIZE, size);
        stack.getTag().putInt(GridNetworkNode.NBT_TAB_SELECTED, tabSelected);
        stack.getTag().putInt(GridNetworkNode.NBT_TAB_PAGE, tabPage);

        stack.getTag().put(PortableGrid.NBT_STORAGE_TRACKER, storageTracker.serializeNbt());
        stack.getTag().put(PortableGrid.NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt());

        if (enchants != null) {
            stack.getTag().put(NBT_ENCHANTMENTS, enchants);
        }

        stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(energyStorage -> energyStorage.receiveEnergy(energyStorage.getEnergyStored(), false));

        for (int i = 0; i < 4; ++i) {
            StackUtils.writeItems(filter, i, stack.getTag());
        }

        StackUtils.writeItems(disk, 4, stack.getTag());

        redstoneMode.write(stack.getTag());

        return stack;
    }

    @Override
    public GridType getGridType() {
        return clientGridType != null ? clientGridType : getServerGridType();
    }

    private GridType getServerGridType() {
        return (getDisk().getStackInSlot(0).isEmpty() || ((IStorageDiskProvider) getDisk().getStackInSlot(0).getItem()).getType() == StorageType.ITEM) ? GridType.NORMAL : GridType.FLUID;
    }

    @Nullable
    @Override
    public IStorageCache getStorageCache() {
        return storage != null ? cache : null;
    }

    @Override
    public IStorageCacheListener createListener(ServerPlayerEntity player) {
        return getServerGridType() == GridType.FLUID ? new PortableFluidGridStorageCacheListener(this, player) : new PortableItemGridStorageCacheListener(this, player);
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
    public void addCraftingListener(IGridCraftingListener listener) {
        // NO OP
    }

    @Override
    public void removeCraftingListener(IGridCraftingListener listener) {
        // NO OP
    }

    @Override
    public ITextComponent getTitle() {
        return new TranslationTextComponent("gui.refinedstorage.portable_grid");
    }

    @Override
    public int getViewType() {
        return -1;
    }

    @Override
    public int getSortingType() {
        return world.isRemote ? SORTING_TYPE.getValue() : sortingType;
    }

    @Override
    public int getSortingDirection() {
        return world.isRemote ? SORTING_DIRECTION.getValue() : sortingDirection;
    }

    @Override
    public int getSearchBoxMode() {
        return world.isRemote ? SEARCH_BOX_MODE.getValue() : searchBoxMode;
    }

    @Override
    public int getTabSelected() {
        return world.isRemote ? TAB_SELECTED.getValue() : tabSelected;
    }

    @Override
    public int getTabPage() {
        return world.isRemote ? TAB_PAGE.getValue() : Math.min(tabPage, getTotalTabPages());
    }

    @Override
    public int getTotalTabPages() {
        return (int) Math.floor((float) Math.max(0, tabs.size() - 1) / (float) IGrid.TABS_PER_PAGE);
    }

    @Override
    public int getSize() {
        return world.isRemote ? SIZE.getValue() : size;
    }

    public void setSortingType(int sortingType) {
        this.sortingType = sortingType;
    }

    public void setSortingDirection(int sortingDirection) {
        this.sortingDirection = sortingDirection;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
    }

    public void setTabSelected(int tabSelected) {
        this.tabSelected = tabSelected;
    }

    public void setTabPage(int page) {
        this.tabPage = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void onViewTypeChanged(int type) {
        // NO OP
    }

    @Override
    public void onSortingTypeChanged(int type) {
        TileDataManager.setParameter(SORTING_TYPE, type);
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        TileDataManager.setParameter(SORTING_DIRECTION, direction);
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        TileDataManager.setParameter(SEARCH_BOX_MODE, searchBoxMode);
    }

    @Override
    public void onSizeChanged(int size) {
        TileDataManager.setParameter(SIZE, size);
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        TileDataManager.setParameter(TAB_SELECTED, tab);
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0 && page <= getTotalTabPages()) {
            TileDataManager.setParameter(TilePortableGrid.TAB_PAGE, page);
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
    public IStorageTracker<ItemStack> getItemStorageTracker() {
        return storageTracker;
    }

    @Override
    public IStorageTracker<FluidStack> getFluidStorageTracker() {
        return fluidStorageTracker;
    }

    @Override
    public CraftingInventory getCraftingMatrix() {
        return null;
    }

    @Override
    public CraftResultInventory getCraftingResult() {
        return null;
    }

    @Override
    public void onCraftingMatrixChanged() {
        // NO OP
    }

    @Override
    public void onCrafted(PlayerEntity player) {
        // NO OP
    }

    @Override
    public void onCraftedShift(PlayerEntity player) {
        // NO OP
    }

    @Override
    public void onRecipeTransfer(PlayerEntity player, ItemStack[][] recipe) {
        // NO OP
    }

    @Override
    public void onClosed(PlayerEntity player) {
        // NO OP
    }

    @Override
    public boolean isActive() {
        int stored = !world.isRemote ? energyStorage.getEnergyStored() : ENERGY_STORED.getValue();

        if (getPortableType() != PortableGridType.CREATIVE && RS.INSTANCE.config.portableGridUsesEnergy && stored <= RS.INSTANCE.config.portableGridOpenUsage) {
            return false;
        }

        if (disk.getStackInSlot(0).isEmpty()) {
            return false;
        }

        RedstoneMode redstoneMode = !world.isRemote ? this.redstoneMode : RedstoneMode.getById(REDSTONE_MODE.getValue());

        return redstoneMode.isEnabled(world, pos);
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
        if (RS.INSTANCE.config.portableGridUsesEnergy && getPortableType() != PortableGridType.CREATIVE && redstoneMode.isEnabled(world, pos)) {
            energyStorage.extractEnergy(energy, false);

            checkIfDiskStateChanged();
        }

        checkIfConnectivityChanged();
    }

    @Override
    public int getStored() {
        return storage != null ? storage.getStored() : 0;
    }

    @Override
    public int getCapacity() {
        return storage != null ? storage.getCapacity() : 0;
    }

    @Override
    public boolean hasStorage() {
        return storage != null;
    }

    @Override
    public int getEnergy() {
        if (RS.INSTANCE.config.portableGridUsesEnergy && getPortableType() != PortableGridType.CREATIVE) {
            return energyStorage.getEnergyStored();
        }

        return energyStorage.getMaxEnergyStored();
    }

    private void checkIfDiskStateChanged() {
        PortableGridDiskState newDiskState = getDiskState(this);

        if (this.diskState != newDiskState) {
            this.diskState = newDiskState;

            WorldUtils.updateBlock(world, pos);
        }
    }

    private void checkIfConnectivityChanged() {
        boolean isConnected = getEnergy() != 0;

        if (this.connected != isConnected) {
            this.connected = isConnected;

            WorldUtils.updateBlock(world, pos);
        }
    }

    @Override
    public ItemHandlerBase getDisk() {
        return disk;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        tag.putInt(GridNetworkNode.NBT_SORTING_DIRECTION, sortingDirection);
        tag.putInt(GridNetworkNode.NBT_SORTING_TYPE, sortingType);
        tag.putInt(GridNetworkNode.NBT_SEARCH_BOX_MODE, searchBoxMode);
        tag.putInt(GridNetworkNode.NBT_SIZE, size);
        tag.putInt(GridNetworkNode.NBT_TAB_SELECTED, tabSelected);
        tag.putInt(GridNetworkNode.NBT_TAB_PAGE, tabPage);

        StackUtils.writeItems(disk, 0, tag);
        StackUtils.writeItems(filter, 1, tag);

        tag.putInt(NBT_ENERGY, energyStorage.getEnergyStored());

        redstoneMode.write(tag);

        tag.put(NBT_STORAGE_TRACKER, storageTracker.serializeNbt());
        tag.put(NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt());

        if (enchants != null) {
            tag.put(NBT_ENCHANTMENTS, enchants);
        }

        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        if (tag.contains(GridNetworkNode.NBT_SORTING_DIRECTION)) {
            sortingDirection = tag.getInt(GridNetworkNode.NBT_SORTING_DIRECTION);
        }

        if (tag.contains(GridNetworkNode.NBT_SORTING_TYPE)) {
            sortingType = tag.getInt(GridNetworkNode.NBT_SORTING_TYPE);
        }

        if (tag.contains(GridNetworkNode.NBT_SEARCH_BOX_MODE)) {
            searchBoxMode = tag.getInt(GridNetworkNode.NBT_SEARCH_BOX_MODE);
        }

        if (tag.contains(GridNetworkNode.NBT_SIZE)) {
            size = tag.getInt(GridNetworkNode.NBT_SIZE);
        }

        if (tag.contains(GridNetworkNode.NBT_TAB_SELECTED)) {
            tabSelected = tag.getInt(GridNetworkNode.NBT_TAB_SELECTED);
        }

        if (tag.contains(GridNetworkNode.NBT_TAB_PAGE)) {
            tabPage = tag.getInt(GridNetworkNode.NBT_TAB_PAGE);
        }

        StackUtils.readItems(disk, 0, tag);
        StackUtils.readItems(filter, 1, tag);

        if (tag.contains(NBT_ENERGY)) {
            energyStorage = recreateEnergyStorage(tag.getInt(NBT_ENERGY));
        }

        redstoneMode = RedstoneMode.read(tag);

        if (tag.contains(NBT_STORAGE_TRACKER)) {
            storageTracker.readFromNbt(tag.getList(NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }

        if (tag.contains(NBT_FLUID_STORAGE_TRACKER)) {
            fluidStorageTracker.readFromNbt(tag.getList(NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }

        if (tag.contains(NBT_ENCHANTMENTS)) {
            enchants = tag.getList(NBT_ENCHANTMENTS, Constants.NBT.TAG_COMPOUND);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();

        this.loadStorage();

        this.connected = getEnergy() != 0;
        this.diskState = getDiskState(this);
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        tag.putInt(NBT_DISK_STATE, diskState.getId());
        tag.putBoolean(NBT_CONNECTED, getEnergy() != 0);
        tag.putInt(NBT_TYPE, getServerGridType().ordinal());

        return super.writeUpdate(tag);
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        super.readUpdate(tag);

        diskState = PortableGridDiskState.getById(tag.getInt(NBT_DISK_STATE));
        connected = tag.getBoolean(NBT_CONNECTED);
        clientGridType = GridType.values()[tag.getInt(NBT_TYPE)];
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyStorageCap.cast();
        }

        return super.getCapability(cap);
    }

    public void onOpened() {
        drainEnergy(RS.INSTANCE.config.portableGridOpenUsage);
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        this.redstoneMode = mode;

        markDirty();
    }

    public static PortableGridDiskState getDiskState(IPortableGridRenderInfo renderInfo) {
        if (!renderInfo.hasStorage()) {
            return PortableGridDiskState.NONE;
        }

        if (!renderInfo.isActive()) {
            return PortableGridDiskState.DISCONNECTED;
        }

        if (renderInfo.getStored() == renderInfo.getCapacity()) {
            return PortableGridDiskState.FULL;
        } else if ((int) ((float) renderInfo.getStored() / (float) renderInfo.getCapacity() * 100F) >= DiskDriveNetworkNode.DiskState.DISK_NEAR_CAPACITY_THRESHOLD) {
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
