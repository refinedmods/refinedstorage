package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.*;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.ItemGridHandlerPortable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.NetworkNodeDiskDrive;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheItemPortable;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageDiskItemPortable;
import com.raoulvdberge.refinedstorage.block.BlockPortableGrid;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.block.PortableGridType;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.integration.forgeenergy.EnergyForge;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerTile;
import com.raoulvdberge.refinedstorage.item.ItemBlockPortableGrid;
import com.raoulvdberge.refinedstorage.item.ItemEnergyItem;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import com.raoulvdberge.refinedstorage.item.filter.Filter;
import com.raoulvdberge.refinedstorage.item.filter.FilterTab;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TilePortableGrid extends TileBase implements IGrid, IPortableGrid, IRedstoneConfigurable {
    public static final TileDataParameter<Integer> ENERGY_STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TilePortableGrid>() {
        @Override
        public Integer getValue(TilePortableGrid tile) {
            return tile.energyStorage.getEnergyStored();
        }
    });

    public static final TileDataParameter<Integer> SORTING_DIRECTION = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TilePortableGrid>() {
        @Override
        public Integer getValue(TilePortableGrid tile) {
            return tile.getSortingDirection();
        }
    }, new ITileDataConsumer<Integer, TilePortableGrid>() {
        @Override
        public void setValue(TilePortableGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSortingDirection(value)) {
                tile.setSortingDirection(value);
                tile.markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SORTING_TYPE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TilePortableGrid>() {
        @Override
        public Integer getValue(TilePortableGrid tile) {
            return tile.getSortingType();
        }
    }, new ITileDataConsumer<Integer, TilePortableGrid>() {
        @Override
        public void setValue(TilePortableGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSortingType(value)) {
                tile.setSortingType(value);
                tile.markDirty();
            }
        }
    }, parameter -> GuiGrid.markForSorting());

    public static final TileDataParameter<Integer> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TilePortableGrid>() {
        @Override
        public Integer getValue(TilePortableGrid tile) {
            return tile.getSearchBoxMode();
        }
    }, new ITileDataConsumer<Integer, TilePortableGrid>() {
        @Override
        public void setValue(TilePortableGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSearchBoxMode(value)) {
                tile.setSearchBoxMode(value);
                tile.markDirty();
            }
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGrid) {
            ((GuiGrid) Minecraft.getMinecraft().currentScreen).updateSearchFieldFocus(parameter.getValue());
        }
    });

    public static final TileDataParameter<Integer> SIZE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TilePortableGrid>() {
        @Override
        public Integer getValue(TilePortableGrid tile) {
            return tile.getSize();
        }
    }, new ITileDataConsumer<Integer, TilePortableGrid>() {
        @Override
        public void setValue(TilePortableGrid tile, Integer value) {
            if (NetworkNodeGrid.isValidSize(value)) {
                tile.setSize(value);
                tile.markDirty();
            }
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().currentScreen.initGui();
        }
    });

    public static final TileDataParameter<Integer> TAB_SELECTED = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TilePortableGrid>() {
        @Override
        public Integer getValue(TilePortableGrid tile) {
            return tile.getTabSelected();
        }
    }, new ITileDataConsumer<Integer, TilePortableGrid>() {
        @Override
        public void setValue(TilePortableGrid tile, Integer value) {
            tile.setTabSelected(value == tile.getTabSelected() ? -1 : value);
            tile.markDirty();
        }
    }, parameter -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGrid) {
            ((GuiGrid) Minecraft.getMinecraft().currentScreen).markForSorting();
        }
    });

    public static final TileDataParameter<Integer> REDSTONE_MODE = RedstoneMode.createParameter();

    private static final String NBT_ENERGY = "Energy";

    private EnergyForge energyStorage = new EnergyForge(ItemEnergyItem.CAPACITY);
    private PortableGridType type;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int size;

    private List<Filter> filters = new ArrayList<>();
    private List<FilterTab> tabs = new ArrayList<>();
    private ItemHandlerFilter filter = new ItemHandlerFilter(filters, tabs, new ItemHandlerListenerTile(this));
    private ItemHandlerBase disk = new ItemHandlerBase(1, new ItemHandlerListenerTile(this), s -> NetworkNodeDiskDrive.VALIDATOR_STORAGE_DISK.test(s) && ((IStorageDiskProvider) s.getItem()).create(s).getType() == StorageDiskType.ITEMS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                if (getStackInSlot(slot).isEmpty()) {
                    storage = null;
                } else {
                    IStorageDiskProvider provider = (IStorageDiskProvider) getStackInSlot(slot).getItem();

                    storage = new StorageDiskItemPortable(provider.create(getStackInSlot(slot)), TilePortableGrid.this);
                    storage.readFromNBT();
                    storage.onPassContainerContext(TilePortableGrid.this::markDirty, () -> false, () -> AccessType.INSERT_EXTRACT);
                }

                cache.invalidate();
            }
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (storage != null) {
                storage.writeToNBT();
            }

            return super.extractItem(slot, amount, simulate);
        }
    };

    @Nullable
    private IStorageDisk<ItemStack> storage;
    private StorageCacheItemPortable cache = new StorageCacheItemPortable(this);
    private ItemGridHandlerPortable handler = new ItemGridHandlerPortable(this, this);

    public TilePortableGrid() {
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
        dataManager.addWatchedParameter(SIZE);
        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(REDSTONE_MODE);
    }

    public PortableGridType getPortableType() {
        if (type == null && getWorld().getBlockState(pos).getBlock() == RSBlocks.PORTABLE_GRID) {
            this.type = (PortableGridType) getWorld().getBlockState(pos).getValue(BlockPortableGrid.TYPE);
        }

        return type == null ? PortableGridType.NORMAL : type;
    }

    public void onPassItemContext(ItemStack stack) {
        this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
        this.tabSelected = ItemWirelessGrid.getTabSelected(stack);
        this.size = ItemWirelessGrid.getSize(stack);

        this.energyStorage.setEnergyStored(stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored());

        for (int i = 0; i < 4; ++i) {
            RSUtils.readItems(filter, i, stack.getTagCompound());
        }

        RSUtils.readItems(disk, 4, stack.getTagCompound());

        this.redstoneMode = RedstoneMode.read(stack.getTagCompound());

        markDirty();
    }

    public ItemStack getAsItem() {
        if (storage != null) {
            storage.writeToNBT();
        }

        ItemStack stack = new ItemStack(RSBlocks.PORTABLE_GRID, 1, getPortableType() == PortableGridType.NORMAL ? ItemBlockPortableGrid.TYPE_NORMAL : ItemBlockPortableGrid.TYPE_CREATIVE);

        stack.setTagCompound(new NBTTagCompound());

        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SORTING_DIRECTION, sortingDirection);
        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SORTING_TYPE, sortingType);
        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SEARCH_BOX_MODE, searchBoxMode);
        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SIZE, size);
        stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_TAB_SELECTED, tabSelected);

        stack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(energyStorage.getEnergyStored(), false);

        for (int i = 0; i < 4; ++i) {
            RSUtils.writeItems(filter, i, stack.getTagCompound());
        }

        RSUtils.writeItems(disk, 4, stack.getTagCompound());

        redstoneMode.write(stack.getTagCompound());

        return stack;
    }

    @Override
    public GridType getType() {
        return GridType.NORMAL;
    }

    @Nullable
    @Override
    public INetworkMaster getNetwork() {
        return null;
    }

    @Nullable
    @Override
    public IItemGridHandler getItemHandler() {
        return handler;
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
        return getWorld().isRemote ? SORTING_TYPE.getValue() : sortingType;
    }

    @Override
    public int getSortingDirection() {
        return getWorld().isRemote ? SORTING_DIRECTION.getValue() : sortingDirection;
    }

    @Override
    public int getSearchBoxMode() {
        return getWorld().isRemote ? SEARCH_BOX_MODE.getValue() : searchBoxMode;
    }

    @Override
    public int getTabSelected() {
        return getWorld().isRemote ? TAB_SELECTED.getValue() : tabSelected;
    }

    @Override
    public int getSize() {
        return getWorld().isRemote ? SIZE.getValue() : size;
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
    public List<Filter> getFilters() {
        return filters;
    }

    @Override
    public List<FilterTab> getTabs() {
        return tabs;
    }

    @Override
    public ItemHandlerBase getFilter() {
        return filter;
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeConfig() {
        return REDSTONE_MODE;
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
        int stored = !getWorld().isRemote ? energyStorage.getEnergyStored() : ENERGY_STORED.getValue();

        if (getPortableType() != PortableGridType.CREATIVE && RS.INSTANCE.config.portableGridUsesEnergy && stored <= RS.INSTANCE.config.portableGridOpenUsage) {
            return false;
        }

        RedstoneMode redstoneMode = !getWorld().isRemote ? this.redstoneMode : RedstoneMode.getById(REDSTONE_MODE.getValue());

        return redstoneMode.isEnabled(getWorld(), pos);
    }

    @Override
    public IStorageCache<ItemStack> getCache() {
        return cache;
    }

    @Override
    public IStorage<ItemStack> getStorage() {
        return storage;
    }

    @Override
    public List<EntityPlayer> getWatchers() {
        return dataManager.getWatchers();
    }

    @Override
    public void drainEnergy(int energy) {
        if (RS.INSTANCE.config.portableGridUsesEnergy && getPortableType() != PortableGridType.CREATIVE && redstoneMode.isEnabled(getWorld(), pos)) {
            energyStorage.extractEnergyInternal(energy);
        }
    }

    @Override
    public ItemHandlerBase getDisk() {
        return disk;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        if (storage != null) {
            storage.writeToNBT();
        }

        tag.setInteger(NetworkNodeGrid.NBT_SORTING_DIRECTION, sortingDirection);
        tag.setInteger(NetworkNodeGrid.NBT_SORTING_TYPE, sortingType);
        tag.setInteger(NetworkNodeGrid.NBT_SEARCH_BOX_MODE, searchBoxMode);
        tag.setInteger(NetworkNodeGrid.NBT_SIZE, size);
        tag.setInteger(NetworkNodeGrid.NBT_TAB_SELECTED, tabSelected);

        RSUtils.writeItems(disk, 0, tag);
        RSUtils.writeItems(filter, 1, tag);

        tag.setInteger(NBT_ENERGY, energyStorage.getEnergyStored());

        redstoneMode.write(tag);

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

        RSUtils.readItems(disk, 0, tag);
        RSUtils.readItems(filter, 1, tag);

        if (tag.hasKey(NBT_ENERGY)) {
            energyStorage.setEnergyStored(tag.getInteger(NBT_ENERGY));
        }

        redstoneMode = RedstoneMode.read(tag);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }

        return super.getCapability(capability, facing);
    }

    public void onOpened(EntityPlayer player) {
        cache.sendUpdateTo(player);

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
}
