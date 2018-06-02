package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.storage.disk.StorageDiskType;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.ItemGridHandlerPortable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.NetworkNodeDiskDrive;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheItemPortable;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheListenerGridPortable;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageTrackerItem;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskItemPortable;
import com.raoulvdberge.refinedstorage.block.BlockPortableGrid;
import com.raoulvdberge.refinedstorage.block.PortableGridDiskState;
import com.raoulvdberge.refinedstorage.block.PortableGridType;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.integration.forgeenergy.EnergyForge;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerTile;
import com.raoulvdberge.refinedstorage.item.ItemBlockPortableGrid;
import com.raoulvdberge.refinedstorage.item.ItemEnergyItem;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TilePortableGrid extends TileBase implements IGrid, IPortableGrid, IRedstoneConfigurable, IStorageDiskContainerContext {
    public static final TileDataParameter<Integer, TilePortableGrid> REDSTONE_MODE = RedstoneMode.createParameter();
    public static final TileDataParameter<Integer, TilePortableGrid> ENERGY_STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.energyStorage.getEnergyStored());
    public static final TileDataParameter<Integer, TilePortableGrid> SORTING_DIRECTION = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSortingDirection, (t, v) -> {
        if (IGrid.isValidSortingDirection(v)) {
            t.setSortingDirection(v);
            t.markDirty();
        }
    }, (initial, p) -> TileGrid.trySortGrid(initial));
    public static final TileDataParameter<Integer, TilePortableGrid> SORTING_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSortingType, (t, v) -> {
        if (IGrid.isValidSortingType(v)) {
            t.setSortingType(v);
            t.markDirty();
        }
    }, (initial, p) -> TileGrid.trySortGrid(initial));
    public static final TileDataParameter<Integer, TilePortableGrid> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSearchBoxMode, (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.setSearchBoxMode(v);
            t.markDirty();
        }
    }, (initial, p) -> GuiBase.executeLater(GuiGrid.class, grid -> grid.updateSearchFieldFocus(p)));
    public static final TileDataParameter<Integer, TilePortableGrid> SIZE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSize, (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.setSize(v);
            t.markDirty();
        }
    }, (initial, p) -> GuiBase.executeLater(GuiGrid.class, GuiBase::initGui));
    public static final TileDataParameter<Integer, TilePortableGrid> TAB_SELECTED = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getTabSelected, (t, v) -> {
        t.setTabSelected(v == t.getTabSelected() ? -1 : v);
        t.markDirty();
    }, (initial, p) -> {
        if (p != -1) {
            GuiBase.executeLater(GuiGrid.class, grid -> grid.getView().sort());
        }
    });
    public static final TileDataParameter<Integer, TilePortableGrid> TAB_PAGE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getTabPage, (t, v) -> {
        if (v >= 0 && v <= t.getTotalTabPages()) {
            t.setTabPage(v);
            t.markDirty();
        }
    });

    private static final String NBT_ENERGY = "Energy";
    private static final String NBT_DISK_STATE = "DiskState";
    private static final String NBT_CONNECTED = "Connected";
    private static final String NBT_STORAGE_TRACKER = "StorageTracker";

    private EnergyForge energyStorage = new EnergyForge(ItemEnergyItem.CAPACITY);
    private PortableGridType type;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;

    private List<IFilter> filters = new ArrayList<>();
    private List<IGridTab> tabs = new ArrayList<>();
    private ItemHandlerFilter filter = new ItemHandlerFilter(filters, tabs, new ItemHandlerListenerTile(this));
    private ItemHandlerBase disk = new ItemHandlerBase(1, new ItemHandlerListenerTile(this), s -> NetworkNodeDiskDrive.VALIDATOR_STORAGE_DISK.test(s) && ((IStorageDiskProvider) s.getItem()).getType() == StorageDiskType.ITEM) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (world != null && !world.isRemote) {
                loadStorage();
            }
        }
    };

    @Nullable
    private IStorageDisk<ItemStack> storage;

    private StorageCacheItemPortable cache = new StorageCacheItemPortable(this);
    private ItemGridHandlerPortable handler = new ItemGridHandlerPortable(this, this);
    private PortableGridDiskState diskState = PortableGridDiskState.NONE;
    private boolean connected;

    private StorageTrackerItem storageTracker = new StorageTrackerItem(this::markDirty);

    public TilePortableGrid() {
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
        } else {
            IStorageDisk disk = API.instance().getStorageDiskManager(world).getByStack(getDisk().getStackInSlot(0));

            if (disk != null) {
                this.storage = new StorageDiskItemPortable(disk, this);
                this.storage.setSettings(TilePortableGrid.this::checkIfDiskStateChanged, TilePortableGrid.this);
            } else {
                this.storage = null;
            }
        }

        this.cache.invalidate();

        this.checkIfDiskStateChanged();
    }

    public PortableGridDiskState getDiskState() {
        return diskState;
    }

    public boolean isConnected() {
        return connected;
    }

    public PortableGridType getPortableType() {
        if (type == null && world.getBlockState(pos).getBlock() == RSBlocks.PORTABLE_GRID) {
            this.type = (PortableGridType) world.getBlockState(pos).getValue(BlockPortableGrid.TYPE);
        }

        return type == null ? PortableGridType.NORMAL : type;
    }

    public void onPassItemContext(ItemStack stack) {
        this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
        this.tabSelected = ItemWirelessGrid.getTabSelected(stack);
        this.tabPage = ItemWirelessGrid.getTabPage(stack);
        this.size = ItemWirelessGrid.getSize(stack);

        this.energyStorage.setEnergyStored(stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored());

        if (stack.hasTagCompound()) {
            for (int i = 0; i < 4; ++i) {
                StackUtils.readItems(filter, i, stack.getTagCompound());
            }

            StackUtils.readItems(disk, 4, stack.getTagCompound());

            this.redstoneMode = RedstoneMode.read(stack.getTagCompound());

            if (stack.getTagCompound().hasKey(PortableGrid.NBT_STORAGE_TRACKER)) {
                storageTracker.readFromNBT(stack.getTagCompound().getTagList(PortableGrid.NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
            }
        }

        this.diskState = getDiskState(this);

        markDirty();
    }

    public ItemStack getAsItem() {
        ItemStack stack = new ItemStack(RSBlocks.PORTABLE_GRID, 1, getPortableType() == PortableGridType.NORMAL ? ItemBlockPortableGrid.TYPE_NORMAL : ItemBlockPortableGrid.TYPE_CREATIVE);

        stack.setTagCompound(new NBTTagCompound());

        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SORTING_DIRECTION, sortingDirection);
        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SORTING_TYPE, sortingType);
        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SEARCH_BOX_MODE, searchBoxMode);
        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SIZE, size);
        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_TAB_SELECTED, tabSelected);
        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_TAB_PAGE, tabPage);

        stack.getTagCompound().setTag(PortableGrid.NBT_STORAGE_TRACKER, storageTracker.serializeNBT());

        stack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(energyStorage.getEnergyStored(), false);

        for (int i = 0; i < 4; ++i) {
            StackUtils.writeItems(filter, i, stack.getTagCompound());
        }

        StackUtils.writeItems(disk, 4, stack.getTagCompound());

        redstoneMode.write(stack.getTagCompound());

        return stack;
    }

    @Override
    public GridType getType() {
        return GridType.NORMAL;
    }

    @Nullable
    @Override
    public IStorageCache getStorageCache() {
        return storage != null ? cache : null;
    }

    @Override
    public IStorageCacheListener createListener(EntityPlayerMP player) {
        return new StorageCacheListenerGridPortable(this, player);
    }

    @Nullable
    @Override
    public IItemGridHandler getItemHandler() {
        return handler;
    }

    @Nullable
    @Override
    public IFluidGridHandler getFluidHandler() {
        return null;
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:portable_grid";
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
    public StorageTrackerItem getStorageTracker() {
        return storageTracker;
    }

    @Override
    public InventoryCrafting getCraftingMatrix() {
        return null;
    }

    @Override
    public InventoryCraftResult getCraftingResult() {
        return null;
    }

    @Override
    public void onCraftingMatrixChanged() {
        // NO OP
    }

    @Override
    public void onCrafted(EntityPlayer player) {
        // NO OP
    }

    @Override
    public void onCraftedShift(EntityPlayer player) {
        // NO OP
    }

    @Override
    public void onRecipeTransfer(EntityPlayer player, ItemStack[][] recipe) {
        // NO OP
    }

    @Override
    public void onClosed(EntityPlayer player) {
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
    public IStorageCache<ItemStack> getCache() {
        return cache;
    }

    @Override
    @Nullable
    public IStorageDisk<ItemStack> getStorage() {
        return storage;
    }

    @Override
    public void drainEnergy(int energy) {
        if (RS.INSTANCE.config.portableGridUsesEnergy && getPortableType() != PortableGridType.CREATIVE && redstoneMode.isEnabled(world, pos)) {
            energyStorage.extractEnergyInternal(energy);

            checkIfDiskStateChanged();
        }

        checkIfConnectivityChanged();
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
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NetworkNodeGrid.NBT_SORTING_DIRECTION, sortingDirection);
        tag.setInteger(NetworkNodeGrid.NBT_SORTING_TYPE, sortingType);
        tag.setInteger(NetworkNodeGrid.NBT_SEARCH_BOX_MODE, searchBoxMode);
        tag.setInteger(NetworkNodeGrid.NBT_SIZE, size);
        tag.setInteger(NetworkNodeGrid.NBT_TAB_SELECTED, tabSelected);
        tag.setInteger(NetworkNodeGrid.NBT_TAB_PAGE, tabPage);

        StackUtils.writeItems(disk, 0, tag);
        StackUtils.writeItems(filter, 1, tag);

        tag.setInteger(NBT_ENERGY, energyStorage.getEnergyStored());

        redstoneMode.write(tag);

        tag.setTag(NBT_STORAGE_TRACKER, storageTracker.serializeNBT());

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NetworkNodeGrid.NBT_SORTING_DIRECTION)) {
            sortingDirection = tag.getInteger(NetworkNodeGrid.NBT_SORTING_DIRECTION);
        }

        if (tag.hasKey(NetworkNodeGrid.NBT_SORTING_TYPE)) {
            sortingType = tag.getInteger(NetworkNodeGrid.NBT_SORTING_TYPE);
        }

        if (tag.hasKey(NetworkNodeGrid.NBT_SEARCH_BOX_MODE)) {
            searchBoxMode = tag.getInteger(NetworkNodeGrid.NBT_SEARCH_BOX_MODE);
        }

        if (tag.hasKey(NetworkNodeGrid.NBT_SIZE)) {
            size = tag.getInteger(NetworkNodeGrid.NBT_SIZE);
        }

        if (tag.hasKey(NetworkNodeGrid.NBT_TAB_SELECTED)) {
            tabSelected = tag.getInteger(NetworkNodeGrid.NBT_TAB_SELECTED);
        }

        if (tag.hasKey(NetworkNodeGrid.NBT_TAB_PAGE)) {
            tabPage = tag.getInteger(NetworkNodeGrid.NBT_TAB_PAGE);
        }

        StackUtils.readItems(disk, 0, tag);
        StackUtils.readItems(filter, 1, tag);

        if (tag.hasKey(NBT_ENERGY)) {
            energyStorage.setEnergyStored(tag.getInteger(NBT_ENERGY));
        }

        redstoneMode = RedstoneMode.read(tag);

        if (tag.hasKey(NBT_STORAGE_TRACKER)) {
            storageTracker.readFromNBT(tag.getTagList(NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
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
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        tag.setInteger(NBT_DISK_STATE, diskState.getId());
        tag.setBoolean(NBT_CONNECTED, getEnergy() != 0);

        return super.writeUpdate(tag);
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        super.readUpdate(tag);

        diskState = PortableGridDiskState.getById(tag.getInteger(NBT_DISK_STATE));
        connected = tag.getBoolean(NBT_CONNECTED);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY ? CapabilityEnergy.ENERGY.cast(energyStorage) : super.getCapability(capability, facing);
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

    public static PortableGridDiskState getDiskState(IPortableGrid portableGrid) {
        if (portableGrid.getStorage() == null) {
            return PortableGridDiskState.NONE;
        }

        if (portableGrid.getEnergy() == 0) {
            return PortableGridDiskState.DISCONNECTED;
        }

        int stored = portableGrid.getStorage().getStored();
        int capacity = portableGrid.getStorage().getCapacity();

        if (stored == capacity) {
            return PortableGridDiskState.FULL;
        } else if ((int) ((float) stored / (float) capacity * 100F) >= 85) {
            return PortableGridDiskState.NEAR_CAPACITY;
        } else {
            return PortableGridDiskState.NORMAL;
        }
    }

    @Override
    public boolean isVoidExcess() {
        return false;
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.INSERT_EXTRACT;
    }
}
