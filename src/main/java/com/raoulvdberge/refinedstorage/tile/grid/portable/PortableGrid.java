package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.ICraftingGridListener;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
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
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.PortableFluidGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.PortableItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.DiskDriveNetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.PortableFluidStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.PortableItemStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.listener.PortableFluidGridStorageCacheListener;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.listener.PortableItemGridStorageCacheListener;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.PortableFluidStorageDisk;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.PortableItemStorageDisk;
import com.raoulvdberge.refinedstorage.apiimpl.storage.tracker.FluidStorageTracker;
import com.raoulvdberge.refinedstorage.apiimpl.storage.tracker.ItemStorageTracker;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PortableGrid implements IGrid, IPortableGrid, IStorageDiskContainerContext {
    public static int ID;

    static final String NBT_STORAGE_TRACKER = "StorageTracker";
    static final String NBT_FLUID_STORAGE_TRACKER = "FluidStorageTracker";

    @Nullable
    private IStorageDisk storage;
    @Nullable
    private IStorageCache cache;

    private PortableItemGridHandler itemHandler = new PortableItemGridHandler(this, this);
    private PortableFluidGridHandler fluidHandler = new PortableFluidGridHandler(this);

    private PlayerEntity player;
    private ItemStack stack;

    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;

    private ItemStorageTracker storageTracker = new ItemStorageTracker(() -> stack.getTag().put(NBT_STORAGE_TRACKER, getItemStorageTracker().serializeNbt()));
    private FluidStorageTracker fluidStorageTracker = new FluidStorageTracker(() -> stack.getTag().put(NBT_FLUID_STORAGE_TRACKER, getFluidStorageTracker().serializeNbt()));

    private List<IFilter> filters = new ArrayList<>();
    private List<IGridTab> tabs = new ArrayList<>();
    private ItemHandlerFilter filter = new ItemHandlerFilter(filters, tabs, null) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!stack.hasTag()) {
                stack.setTag(new CompoundNBT());
            }

            StackUtils.writeItems(this, 0, stack.getTag());
        }
    };
    private ItemHandlerBase disk = new ItemHandlerBase(1, DiskDriveNetworkNode.VALIDATOR_STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (/*TODO FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER*/false) {
                ItemStack diskStack = getStackInSlot(slot);

                if (diskStack.isEmpty()) {
                    storage = null;
                    cache = null;
                } else {
                    IStorageDisk disk = API.instance().getStorageDiskManager((ServerWorld) player.world).getByStack(getDisk().getStackInSlot(0));

                    if (disk != null) {
                        StorageType type = ((IStorageDiskProvider) getDisk().getStackInSlot(0).getItem()).getType();

                        switch (type) {
                            case ITEM:
                                storage = new PortableItemStorageDisk(disk, PortableGrid.this);
                                cache = new PortableItemStorageCache(PortableGrid.this);
                                break;
                            case FLUID:
                                storage = new PortableFluidStorageDisk(disk, PortableGrid.this);
                                cache = new PortableFluidStorageCache(PortableGrid.this);
                                break;
                        }

                        storage.setSettings(null, PortableGrid.this);
                    } else {
                        storage = null;
                        cache = null;
                    }
                }

                if (cache != null) {
                    cache.invalidate();
                }

                StackUtils.writeItems(this, 4, stack.getTag());
            }
        }
    };

    public PortableGrid(PlayerEntity player, ItemStack stack) {
        this.player = player;
        this.stack = stack;

        if (player != null) {
            /* TODO this.sortingType = ItemWirelessGrid.getSortingType(stack);
            this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
            this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
            this.tabSelected = ItemWirelessGrid.getTabSelected(stack);
            this.tabPage = ItemWirelessGrid.getTabPage(stack);
            this.size = ItemWirelessGrid.getSize(stack);*/
        }

        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        if (stack.getTag().contains(NBT_STORAGE_TRACKER)) {
            storageTracker.readFromNbt(stack.getTag().getList(NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }

        if (stack.getTag().contains(NBT_FLUID_STORAGE_TRACKER)) {
            fluidStorageTracker.readFromNbt(stack.getTag().getList(NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }

        StackUtils.readItems(disk, 4, stack.getTag());

        StackUtils.readItems(filter, 0, stack.getTag());

        drainEnergy(RS.INSTANCE.config.portableGridOpenUsage);
    }

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
        //TODO if (RS.INSTANCE.config.portableGridUsesEnergy && stack.getItemDamage() != ItemBlockPortableGrid.TYPE_CREATIVE) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        //}
        if (storage != null) {
            storage.extractEnergy(energy, false);
        }
    }

    @Override
    public int getEnergy() {
        //TODO if (RS.INSTANCE.config.portableGridUsesEnergy && stack.getItemDamage() != ItemBlockPortableGrid.TYPE_CREATIVE) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
        // }

        return storage == null ? RS.INSTANCE.config.portableGridCapacity : storage.getMaxEnergyStored();
    }

    @Override
    public ItemHandlerBase getDisk() {
        return disk;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    public GridType getGridType() {
        return (getDisk().getStackInSlot(0).isEmpty() || ((IStorageDiskProvider) getDisk().getStackInSlot(0).getItem()).getType() == StorageType.ITEM) ? GridType.NORMAL : GridType.FLUID;
    }

    @Nullable
    @Override
    public IStorageCache getStorageCache() {
        return storage != null ? cache : null;
    }

    @Override
    public IStorageCacheListener createListener(ServerPlayerEntity player) {
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
    public ITextComponent getTitle() {
        return new TranslationTextComponent("gui.refinedstorage.portable_grid");
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
        // TODO RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), type, getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingType = type;

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        // TODO RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), direction, getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingDirection = direction;

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        // TODO RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), searchBoxMode, getSize(), getTabSelected(), getTabPage()));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void onSizeChanged(int size) {
        // TODO RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), size, getTabSelected(), getTabPage()));

        this.size = size;

        // TODO if (Minecraft.getMinecraft().currentScreen != null) {
        //    Minecraft.getMinecraft().currentScreen.initGui();
        //}
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        this.tabSelected = tab == tabSelected ? -1 : tab;

        // TODO RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), tabSelected, getTabPage()));

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0 && page <= getTotalTabPages()) {
            // TODO RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), page));

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
        return storageTracker;
    }

    @Override
    public FluidStorageTracker getFluidStorageTracker() {
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
    public void onClear(PlayerEntity player) {
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
        if (!player.getEntityWorld().isRemote) {
            StackUtils.writeItems(disk, 4, stack.getTag());
        }
    }

    @Override
    public boolean isActive() {
        // TODO if (RS.INSTANCE.config.portableGridUsesEnergy && stack.getItemDamage() != ItemBlockPortableGrid.TYPE_CREATIVE && stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() <= RS.INSTANCE.config.portableGridOpenUsage) {
        //  return false;
        //+}

        if (disk.getStackInSlot(0).isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.INSERT_EXTRACT;
    }
}
