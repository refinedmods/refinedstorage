package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridCraftingListener;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.*;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.FluidGridHandlerPortable;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.ItemGridHandlerPortable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.NetworkNodeDiskDrive;
import com.raoulvdberge.refinedstorage.apiimpl.storage.*;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFluidPortable;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskItemPortable;
import com.raoulvdberge.refinedstorage.block.BlockPortableGrid;
import com.raoulvdberge.refinedstorage.block.enums.PortableGridDiskState;
import com.raoulvdberge.refinedstorage.block.enums.PortableGridType;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerTile;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import com.raoulvdberge.refinedstorage.item.itemblock.ItemBlockPortableGrid;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsDisk;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TilePortableGrid extends TileBase implements IGrid, IPortableGrid, IRedstoneConfigurable, IStorageDiskContainerContext, IPortableGrid.IPortableGridRenderInfo {
    public static final TileDataParameter<Integer, TilePortableGrid> REDSTONE_MODE = RedstoneMode.createParameter();
    private static final TileDataParameter<Integer, TilePortableGrid> ENERGY_STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.energyStorage.getEnergyStored());
    private static final TileDataParameter<Integer, TilePortableGrid> SORTING_DIRECTION = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSortingDirection, (t, v) -> {
        if (IGrid.isValidSortingDirection(v)) {
            t.setSortingDirection(v);
            t.markDirty();
        }
    }, (initial, p) -> TileGrid.trySortGrid(initial));
    private static final TileDataParameter<Integer, TilePortableGrid> SORTING_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSortingType, (t, v) -> {
        if (IGrid.isValidSortingType(v)) {
            t.setSortingType(v);
            t.markDirty();
        }
    }, (initial, p) -> TileGrid.trySortGrid(initial));
    private static final TileDataParameter<Integer, TilePortableGrid> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSearchBoxMode, (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.setSearchBoxMode(v);
            t.markDirty();
        }
    }, (initial, p) -> GuiBase.executeLater(GuiGrid.class, grid -> grid.getSearchField().setMode(p)));
    private static final TileDataParameter<Integer, TilePortableGrid> SIZE = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getSize, (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.setSize(v);
            t.markDirty();
        }
    }, (initial, p) -> GuiBase.executeLater(GuiGrid.class, GuiBase::initGui));
    private static final TileDataParameter<Integer, TilePortableGrid> TAB_SELECTED = new TileDataParameter<>(DataSerializers.VARINT, 0, TilePortableGrid::getTabSelected, (t, v) -> {
        t.setTabSelected(v == t.getTabSelected() ? -1 : v);
        t.markDirty();
    }, (initial, p) -> GuiBase.executeLater(GuiGrid.class, grid -> grid.getView().sort()));
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

    private EnergyStorage energyStorage = recreateEnergyStorage(0);
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
    private ItemHandlerBase disk = new ItemHandlerBase(1, new ListenerTile(this), NetworkNodeDiskDrive.VALIDATOR_STORAGE_DISK) {
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

    private StorageTrackerItem storageTracker = new StorageTrackerItem(this::markDirty);
    private StorageTrackerFluid fluidStorageTracker = new StorageTrackerFluid(this::markDirty);

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
            this.cache = null;
        } else {
            IStorageDisk disk = API.instance().getStorageDiskManager(world).getByStack(getDisk().getStackInSlot(0));

            if (disk != null) {
                StorageType type = ((IStorageDiskProvider) getDisk().getStackInSlot(0).getItem()).getType();

                switch (type) {
                    case ITEM:
                        this.storage = new StorageDiskItemPortable(disk, this);
                        this.cache = new StorageCacheItemPortable(this);
                        break;
                    case FLUID:
                        this.storage = new StorageDiskFluidPortable(disk, this);
                        this.cache = new StorageCacheFluidPortable(this);
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
            IBlockState state = world.getBlockState(pos);

            if (state.getBlock() == RSBlocks.PORTABLE_GRID) {
                this.type = (PortableGridType) state.getValue(BlockPortableGrid.TYPE);
            }
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

        this.energyStorage = recreateEnergyStorage(stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored());

        if (stack.hasTagCompound()) {
            for (int i = 0; i < 4; ++i) {
                StackUtils.readItems(filter, i, stack.getTagCompound());
            }

            StackUtils.readItems(disk, 4, stack.getTagCompound());

            this.redstoneMode = RedstoneMode.read(stack.getTagCompound());

            if (stack.getTagCompound().hasKey(PortableGrid.NBT_STORAGE_TRACKER)) {
                storageTracker.readFromNbt(stack.getTagCompound().getTagList(PortableGrid.NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
            }

            if (stack.getTagCompound().hasKey(PortableGrid.NBT_FLUID_STORAGE_TRACKER)) {
                fluidStorageTracker.readFromNbt(stack.getTagCompound().getTagList(PortableGrid.NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
            }
        }

        this.diskState = getDiskState(this);

        markDirty();
    }

    private EnergyStorage recreateEnergyStorage(int energyStored) {
        return new EnergyStorage(RS.INSTANCE.config.portableGridCapacity, RS.INSTANCE.config.portableGridCapacity, 0, energyStored);
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

        stack.getTagCompound().setTag(PortableGrid.NBT_STORAGE_TRACKER, storageTracker.serializeNbt());
        stack.getTagCompound().setTag(PortableGrid.NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt());

        stack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(energyStorage.getEnergyStored(), false);

        for (int i = 0; i < 4; ++i) {
            StackUtils.writeItems(filter, i, stack.getTagCompound());
        }

        StackUtils.writeItems(disk, 4, stack.getTagCompound());

        redstoneMode.write(stack.getTagCompound());

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
    public IStorageCacheListener createListener(EntityPlayerMP player) {
        return getServerGridType() == GridType.FLUID ? new StorageCacheListenerGridPortableFluid(this, player) : new StorageCacheListenerGridPortable(this, player);
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
    public IStorageTracker<ItemStack> getItemStorageTracker() {
        return storageTracker;
    }

    @Override
    public IStorageTracker<FluidStack> getFluidStorageTracker() {
        return fluidStorageTracker;
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

        tag.setTag(NBT_STORAGE_TRACKER, storageTracker.serializeNbt());
        tag.setTag(NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt());

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
            energyStorage = recreateEnergyStorage(tag.getInteger(NBT_ENERGY));
        }

        redstoneMode = RedstoneMode.read(tag);

        if (tag.hasKey(NBT_STORAGE_TRACKER)) {
            storageTracker.readFromNbt(tag.getTagList(NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }

        if (tag.hasKey(NBT_FLUID_STORAGE_TRACKER)) {
            fluidStorageTracker.readFromNbt(tag.getTagList(NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
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
        tag.setInteger(NBT_TYPE, getServerGridType().ordinal());

        return super.writeUpdate(tag);
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        super.readUpdate(tag);

        diskState = PortableGridDiskState.getById(tag.getInteger(NBT_DISK_STATE));
        connected = tag.getBoolean(NBT_CONNECTED);
        clientGridType = GridType.values()[tag.getInteger(NBT_TYPE)];
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

    public static PortableGridDiskState getDiskState(IPortableGridRenderInfo renderInfo) {
        if (!renderInfo.hasStorage()) {
            return PortableGridDiskState.NONE;
        }

        if (!renderInfo.isActive()) {
            return PortableGridDiskState.DISCONNECTED;
        }

        if (renderInfo.getStored() == renderInfo.getCapacity()) {
            return PortableGridDiskState.FULL;
        } else if ((int) ((float) renderInfo.getStored() / (float) renderInfo.getCapacity() * 100F) >= ConstantsDisk.DISK_NEAR_CAPACITY_THRESHOLD) {
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
