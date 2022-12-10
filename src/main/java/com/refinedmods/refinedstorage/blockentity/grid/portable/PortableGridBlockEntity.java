package com.refinedmods.refinedstorage.blockentity.grid.portable;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
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
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.PortableFluidGridHandler;
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.PortableItemGridHandler;
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.PortableFluidStorageCache;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.PortableItemStorageCache;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.PortableFluidGridStorageCacheListener;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.PortableItemGridStorageCacheListener;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.PortableFluidStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.PortableItemStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.tracker.FluidStorageTracker;
import com.refinedmods.refinedstorage.apiimpl.storage.tracker.ItemStorageTracker;
import com.refinedmods.refinedstorage.block.PortableGridBlock;
import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IRedstoneConfigurable;
import com.refinedmods.refinedstorage.blockentity.config.RedstoneMode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.FilterItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.StorageDiskItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.BlockEntityInventoryListener;
import com.refinedmods.refinedstorage.item.WirelessGridItem;
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PortableGridBlockEntity extends BaseBlockEntity implements IGrid, IPortableGrid, IRedstoneConfigurable, IStorageDiskContainerContext {
    private static final String NBT_ITEM_STORAGE_TRACKER_ID = "ItemStorageTrackerId";
    private static final String NBT_FLUID_STORAGE_TRACKER_ID = "FluidStorageTrackerId";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_ENERGY = "Energy";
    private static final String NBT_ENCHANTMENTS = "Enchantments"; // @Volatile: Minecraft specific nbt key, see EnchantmentHelper

    public static final BlockEntitySynchronizationParameter<Integer, PortableGridBlockEntity> REDSTONE_MODE = RedstoneMode.createParameter();

    private static final BlockEntitySynchronizationParameter<Integer, PortableGridBlockEntity> SORTING_DIRECTION = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, PortableGridBlockEntity::getSortingDirection, (t, v) -> {
        if (IGrid.isValidSortingDirection(v)) {
            t.setSortingDirection(v);
            t.setChanged();
        }
    }, (initial, p) -> GridBlockEntity.trySortGrid(initial));

    private static final BlockEntitySynchronizationParameter<Integer, PortableGridBlockEntity> SORTING_TYPE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, PortableGridBlockEntity::getSortingType, (t, v) -> {
        if (IGrid.isValidSortingType(v)) {
            t.setSortingType(v);
            t.setChanged();
        }
    }, (initial, p) -> GridBlockEntity.trySortGrid(initial));

    private static final BlockEntitySynchronizationParameter<Integer, PortableGridBlockEntity> SEARCH_BOX_MODE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, PortableGridBlockEntity::getSearchBoxMode, (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.setSearchBoxMode(v);
            t.setChanged();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getSearchField().setMode(p)));

    private static final BlockEntitySynchronizationParameter<Integer, PortableGridBlockEntity> SIZE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, PortableGridBlockEntity::getSize, (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.setSize(v);
            t.setChanged();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));

    private static final BlockEntitySynchronizationParameter<Integer, PortableGridBlockEntity> TAB_SELECTED = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, PortableGridBlockEntity::getTabSelected, (t, v) -> {
        t.setTabSelected(v == t.getTabSelected() ? -1 : v);
        t.setChanged();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort()));

    private static final BlockEntitySynchronizationParameter<Integer, PortableGridBlockEntity> TAB_PAGE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, PortableGridBlockEntity::getTabPage, (t, v) -> {
        if (v >= 0 && v <= t.getTotalTabPages()) {
            t.setTabPage(v);
            t.setChanged();
        }
    });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(SORTING_DIRECTION)
        .addWatchedParameter(SORTING_TYPE)
        .addWatchedParameter(SEARCH_BOX_MODE)
        .addWatchedParameter(SIZE)
        .addWatchedParameter(TAB_SELECTED)
        .addWatchedParameter(TAB_PAGE)
        .build();

    private final PortableGridBlockItem.Type type;
    private final List<IFilter> filters = new ArrayList<>();
    private final List<IGridTab> tabs = new ArrayList<>();
    private final FilterItemHandler filter = (FilterItemHandler) new FilterItemHandler(filters, tabs).addListener(new BlockEntityInventoryListener(this));
    private final PortableItemGridHandler itemHandler = new PortableItemGridHandler(this, this);
    private final PortableFluidGridHandler fluidHandler = new PortableFluidGridHandler(this);
    private EnergyStorage energyStorage = createEnergyStorage(0);
    private final LazyOptional<EnergyStorage> energyStorageCap = LazyOptional.of(() -> energyStorage);
    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;
    private GridType clientGridType;
    @Nullable
    private IStorageDisk storage;
    @Nullable
    private IStorageCache cache;
    private PortableGridDiskState diskState = PortableGridDiskState.NONE;
    private boolean active;
    private ItemStorageTracker itemStorageTracker;
    private UUID itemStorageTrackerId;
    private FluidStorageTracker fluidStorageTracker;
    private UUID fluidStorageTrackerId;
    private ListTag enchants = null;
    private boolean loadNextTick;

    public PortableGridBlockEntity(PortableGridBlockItem.Type type, BlockPos pos, BlockState state) {
        super(type == PortableGridBlockItem.Type.CREATIVE ? RSBlockEntities.CREATIVE_PORTABLE_GRID.get() : RSBlockEntities.PORTABLE_GRID.get(), pos, state, SPEC);
        this.type = type;
    }

    public static void serverTick(PortableGridBlockEntity blockEntity) {
        if (blockEntity.loadNextTick) {
            blockEntity.active = blockEntity.isGridActive();
            blockEntity.diskState = blockEntity.getDiskState();
            blockEntity.loadNextTick = false;
        }
    }

    private final BaseItemHandler disk = new BaseItemHandler(1)
        .addValidator(new StorageDiskItemValidator())
        .addListener(new BlockEntityInventoryListener(this))
        .addListener((handler, slot, reading) -> {
            if (level != null && !level.isClientSide) {
                loadStorage();

                if (!reading) {
                    updateState();

                    LevelUtils.updateBlock(level, worldPosition); // Re-send grid type
                }
            }
        });

    private void loadStorage() {
        ItemStack diskStack = getDiskInventory().getStackInSlot(0);

        if (diskStack.isEmpty()) {
            this.storage = null;
            this.cache = null;
        } else {
            IStorageDisk diskInSlot = API.instance().getStorageDiskManager((ServerLevel) level).getByStack(getDiskInventory().getStackInSlot(0));

            if (diskInSlot != null) {
                StorageType diskType = ((IStorageDiskProvider) getDiskInventory().getStackInSlot(0).getItem()).getType();

                if (diskType == StorageType.ITEM) {
                    this.storage = new PortableItemStorageDisk(diskInSlot, this);
                    this.cache = new PortableItemStorageCache(this);
                } else if (diskType == StorageType.FLUID) {
                    this.storage = new PortableFluidStorageDisk(diskInSlot, this);
                    this.cache = new PortableFluidStorageCache(this);
                }

                this.storage.setSettings(PortableGridBlockEntity.this::updateState, PortableGridBlockEntity.this);
            } else {
                this.storage = null;
                this.cache = null;
            }
        }

        if (cache != null) {
            cache.invalidate(InvalidateCause.DISK_INVENTORY_CHANGED);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();

        this.loadStorage();

        loadNextTick = true;
    }

    public void applyDataFromItemToBlockEntity(ItemStack stack) {
        this.sortingType = WirelessGridItem.getSortingType(stack);
        this.sortingDirection = WirelessGridItem.getSortingDirection(stack);
        this.searchBoxMode = WirelessGridItem.getSearchBoxMode(stack);
        this.tabSelected = WirelessGridItem.getTabSelected(stack);
        this.tabPage = WirelessGridItem.getTabPage(stack);
        this.size = WirelessGridItem.getSize(stack);

        this.energyStorage = createEnergyStorage(stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0));

        if (stack.hasTag()) {
            for (int i = 0; i < 4; ++i) {
                StackUtils.readItems(filter, i, stack.getTag());
            }

            StackUtils.readItems(disk, 4, stack.getTag());

            this.redstoneMode = RedstoneMode.read(stack.getTag());
            if (stack.getTag().contains(PortableGrid.NBT_ITEM_STORAGE_TRACKER_ID)) {
                itemStorageTrackerId = stack.getTag().getUUID(NBT_ITEM_STORAGE_TRACKER_ID);
            }

            if (stack.getTag().contains(PortableGrid.NBT_FLUID_STORAGE_TRACKER_ID)) {
                fluidStorageTrackerId = stack.getTag().getUUID(NBT_FLUID_STORAGE_TRACKER_ID);
            }

            if (stack.getTag().contains(NBT_ENCHANTMENTS)) {
                enchants = stack.getTag().getList(NBT_ENCHANTMENTS, Tag.TAG_COMPOUND);
            }
        }

        setChanged();
    }

    public void applyDataFromBlockEntityToItem(ItemStack stack) {
        stack.setTag(new CompoundTag());

        stack.getTag().putInt(GridNetworkNode.NBT_SORTING_DIRECTION, sortingDirection);
        stack.getTag().putInt(GridNetworkNode.NBT_SORTING_TYPE, sortingType);
        stack.getTag().putInt(GridNetworkNode.NBT_SEARCH_BOX_MODE, searchBoxMode);
        stack.getTag().putInt(GridNetworkNode.NBT_SIZE, size);
        stack.getTag().putInt(GridNetworkNode.NBT_TAB_SELECTED, tabSelected);
        stack.getTag().putInt(GridNetworkNode.NBT_TAB_PAGE, tabPage);

        if (itemStorageTrackerId != null) {
            stack.getTag().putUUID(PortableGrid.NBT_ITEM_STORAGE_TRACKER_ID, itemStorageTrackerId);
        }
        if (fluidStorageTrackerId != null) {
            stack.getTag().putUUID(PortableGrid.NBT_FLUID_STORAGE_TRACKER_ID, fluidStorageTrackerId);
        }

        if (enchants != null) {
            stack.getTag().put(NBT_ENCHANTMENTS, enchants);
        }

        stack.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(itemEnergy -> itemEnergy.receiveEnergy(energyStorage.getEnergyStored(), false));

        for (int i = 0; i < 4; ++i) {
            StackUtils.writeItems(filter, i, stack.getTag());
        }

        StackUtils.writeItems(disk, 4, stack.getTag());

        redstoneMode.write(stack.getTag());
    }

    private EnergyStorage createEnergyStorage(int energyStored) {
        return new EnergyStorage(
            RS.SERVER_CONFIG.getPortableGrid().getCapacity(),
            RS.SERVER_CONFIG.getPortableGrid().getCapacity(),
            RS.SERVER_CONFIG.getPortableGrid().getCapacity(),
            energyStored
        );
    }

    @Override
    public GridType getGridType() {
        return clientGridType != null ? clientGridType : getServerGridType();
    }

    private GridType getServerGridType() {
        return (getDiskInventory().getStackInSlot(0).isEmpty() || ((IStorageDiskProvider) getDiskInventory().getStackInSlot(0).getItem()).getType() == StorageType.ITEM) ? GridType.NORMAL : GridType.FLUID;
    }

    @Nullable
    @Override
    public IStorageCache getStorageCache() {
        return storage != null ? cache : null;
    }

    @Override
    public IStorageCacheListener createListener(ServerPlayer player) {
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
        return level.isClientSide ? SORTING_TYPE.getValue() : sortingType;
    }

    public void setSortingType(int sortingType) {
        this.sortingType = sortingType;
    }

    @Override
    public int getSortingDirection() {
        return level.isClientSide ? SORTING_DIRECTION.getValue() : sortingDirection;
    }

    public void setSortingDirection(int sortingDirection) {
        this.sortingDirection = sortingDirection;
    }

    @Override
    public int getSearchBoxMode() {
        return level.isClientSide ? SEARCH_BOX_MODE.getValue() : searchBoxMode;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public int getTabSelected() {
        return level.isClientSide ? TAB_SELECTED.getValue() : tabSelected;
    }

    public void setTabSelected(int tabSelected) {
        this.tabSelected = tabSelected;
    }

    @Override
    public int getTabPage() {
        return level.isClientSide ? TAB_PAGE.getValue() : Math.min(tabPage, getTotalTabPages());
    }

    public void setTabPage(int page) {
        this.tabPage = page;
    }

    @Override
    public int getTotalTabPages() {
        return (int) Math.floor((float) Math.max(0, tabs.size() - 1) / (float) IGrid.TABS_PER_PAGE);
    }

    @Override
    public int getSize() {
        return level.isClientSide ? SIZE.getValue() : size;
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
        BlockEntitySynchronizationManager.setParameter(SORTING_TYPE, type);
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        BlockEntitySynchronizationManager.setParameter(SORTING_DIRECTION, direction);
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        BlockEntitySynchronizationManager.setParameter(SEARCH_BOX_MODE, searchBoxMode);
    }

    @Override
    public void onSizeChanged(int size) {
        BlockEntitySynchronizationManager.setParameter(SIZE, size);
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        BlockEntitySynchronizationManager.setParameter(TAB_SELECTED, tab);
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0 && page <= getTotalTabPages()) {
            BlockEntitySynchronizationManager.setParameter(PortableGridBlockEntity.TAB_PAGE, page);
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
        if (itemStorageTracker == null) {
            if (itemStorageTrackerId == null) {
                this.itemStorageTrackerId = UUID.randomUUID();
            }

            this.itemStorageTracker = (ItemStorageTracker) API.instance().getStorageTrackerManager(ServerLifecycleHooks.getCurrentServer().overworld()).getOrCreate(itemStorageTrackerId, StorageType.ITEM);
        }

        return itemStorageTracker;
    }

    @Override
    public IStorageTracker<FluidStack> getFluidStorageTracker() {
        if (fluidStorageTracker == null) {
            if (fluidStorageTrackerId == null) {
                this.fluidStorageTrackerId = UUID.randomUUID();
            }

            this.fluidStorageTracker = (FluidStorageTracker) API.instance().getStorageTrackerManager(ServerLifecycleHooks.getCurrentServer().overworld()).getOrCreate(fluidStorageTrackerId, StorageType.FLUID);
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
        // NO OP
    }

    private boolean hasDisk() {
        return !disk.getStackInSlot(0).isEmpty();
    }

    @Override
    public boolean isGridActive() {
        if (level.isClientSide) {
            BlockState state = level.getBlockState(worldPosition);

            if (state.getBlock() instanceof PortableGridBlock) {
                return state.getValue(PortableGridBlock.ACTIVE);
            }

            return false;
        }

        if (RS.SERVER_CONFIG.getPortableGrid().getUseEnergy() &&
            type != PortableGridBlockItem.Type.CREATIVE &&
            energyStorage.getEnergyStored() <= RS.SERVER_CONFIG.getPortableGrid().getOpenUsage()) {
            return false;
        }

        if (!hasDisk()) {
            return false;
        }

        return redstoneMode.isEnabled(level.hasNeighborSignal(worldPosition));
    }

    @Override
    public int getSlotId() {
        return -1;
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
        if (RS.SERVER_CONFIG.getPortableGrid().getUseEnergy() &&
            type != PortableGridBlockItem.Type.CREATIVE &&
            redstoneMode.isEnabled(level.hasNeighborSignal(worldPosition))) {
            energyStorage.extractEnergy(energy, false);

            updateState();
        }
    }

    @Override
    public int getEnergy() {
        if (RS.SERVER_CONFIG.getPortableGrid().getUseEnergy() && type != PortableGridBlockItem.Type.CREATIVE) {
            return energyStorage.getEnergyStored();
        }

        return RS.SERVER_CONFIG.getPortableGrid().getCapacity();
    }

    @Override
    public PortableGridDiskState getDiskState() {
        if (!hasDisk()) {
            return PortableGridDiskState.NONE;
        }

        if (!isGridActive()) {
            return PortableGridDiskState.DISCONNECTED;
        }

        int stored = storage != null ? storage.getStored() : 0;
        int capacity = storage != null ? storage.getCapacity() : 0;

        if (stored == capacity) {
            return PortableGridDiskState.FULL;
        } else if ((int) ((float) stored / (float) capacity * 100F) >= DiskState.DISK_NEAR_CAPACITY_THRESHOLD) {
            return PortableGridDiskState.NEAR_CAPACITY;
        } else {
            return PortableGridDiskState.NORMAL;
        }
    }

    public void updateState() {
        PortableGridDiskState newDiskState = getDiskState();

        if (this.diskState != newDiskState) {
            this.diskState = newDiskState;

            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(PortableGridBlock.DISK_STATE, diskState));
        }

        boolean isActive = isGridActive();

        if (this.active != isActive) {
            this.active = isActive;

            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(PortableGridBlock.ACTIVE, active));
        }
    }

    @Override
    public BaseItemHandler getDiskInventory() {
        return disk;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

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

        if (itemStorageTrackerId != null) {
            tag.putUUID(NBT_ITEM_STORAGE_TRACKER_ID, itemStorageTrackerId);
        }
        if (fluidStorageTrackerId != null) {
            tag.putUUID(NBT_FLUID_STORAGE_TRACKER_ID, fluidStorageTrackerId);
        }

        if (enchants != null) {
            tag.put(NBT_ENCHANTMENTS, enchants);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

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
            energyStorage = createEnergyStorage(tag.getInt(NBT_ENERGY));
        }

        redstoneMode = RedstoneMode.read(tag);

        if (tag.contains(NBT_ITEM_STORAGE_TRACKER_ID)) {
            itemStorageTrackerId = tag.getUUID(NBT_ITEM_STORAGE_TRACKER_ID);
        }

        if (tag.contains(NBT_FLUID_STORAGE_TRACKER_ID)) {
            fluidStorageTrackerId = tag.getUUID(NBT_FLUID_STORAGE_TRACKER_ID);
        }

        if (tag.contains(NBT_ENCHANTMENTS)) {
            enchants = tag.getList(NBT_ENCHANTMENTS, Tag.TAG_COMPOUND);
        }
    }

    @Override
    public CompoundTag writeUpdate(CompoundTag tag) {
        tag.putInt(NBT_TYPE, getServerGridType().ordinal());

        return super.writeUpdate(tag);
    }

    @Override
    public void readUpdate(CompoundTag tag) {
        super.readUpdate(tag);

        clientGridType = GridType.values()[tag.getInt(NBT_TYPE)];
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyStorageCap.cast();
        }

        return super.getCapability(cap, direction);
    }

    public void onOpened() {
        drainEnergy(RS.SERVER_CONFIG.getPortableGrid().getOpenUsage());
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        this.redstoneMode = mode;

        setChanged();
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.INSERT_EXTRACT;
    }
}
