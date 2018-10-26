package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridCraftingListener;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.storage.StorageType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.FluidGridHandlerPortable;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.ItemGridHandlerPortable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.NetworkNodeDiskDrive;
import com.raoulvdberge.refinedstorage.apiimpl.storage.*;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFluidPortable;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskItemPortable;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import com.raoulvdberge.refinedstorage.item.itemblock.ItemBlockPortableGrid;
import com.raoulvdberge.refinedstorage.network.MessageGridSettingsUpdate;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
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

    private ItemGridHandlerPortable itemHandler = new ItemGridHandlerPortable(this, this);
    private FluidGridHandlerPortable fluidHandler = new FluidGridHandlerPortable(this);

    private EntityPlayer player;
    private ItemStack stack;

    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;

    private StorageTrackerItem storageTracker = new StorageTrackerItem(() -> stack.getTagCompound().setTag(NBT_STORAGE_TRACKER, getItemStorageTracker().serializeNbt()));
    private StorageTrackerFluid fluidStorageTracker = new StorageTrackerFluid(() -> stack.getTagCompound().setTag(NBT_FLUID_STORAGE_TRACKER, getFluidStorageTracker().serializeNbt()));

    private List<IFilter> filters = new ArrayList<>();
    private List<IGridTab> tabs = new ArrayList<>();
    private ItemHandlerFilter filter = new ItemHandlerFilter(filters, tabs, null) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            StackUtils.writeItems(this, 0, stack.getTagCompound());
        }
    };
    private ItemHandlerBase disk = new ItemHandlerBase(1, NetworkNodeDiskDrive.VALIDATOR_STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                ItemStack diskStack = getStackInSlot(slot);

                if (diskStack.isEmpty()) {
                    storage = null;
                    cache = null;
                } else {
                    IStorageDisk disk = API.instance().getStorageDiskManager(player.getEntityWorld()).getByStack(getDisk().getStackInSlot(0));

                    if (disk != null) {
                        StorageType type = ((IStorageDiskProvider) getDisk().getStackInSlot(0).getItem()).getType();

                        switch (type) {
                            case ITEM:
                                storage = new StorageDiskItemPortable(disk, PortableGrid.this);
                                cache = new StorageCacheItemPortable(PortableGrid.this);
                                break;
                            case FLUID:
                                storage = new StorageDiskFluidPortable(disk, PortableGrid.this);
                                cache = new StorageCacheFluidPortable(PortableGrid.this);
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

                StackUtils.writeItems(this, 4, stack.getTagCompound());
            }
        }
    };

    public PortableGrid(EntityPlayer player, ItemStack stack) {
        this.player = player;
        this.stack = stack;

        if (player != null) {
            this.sortingType = ItemWirelessGrid.getSortingType(stack);
            this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
            this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
            this.tabSelected = ItemWirelessGrid.getTabSelected(stack);
            this.tabPage = ItemWirelessGrid.getTabPage(stack);
            this.size = ItemWirelessGrid.getSize(stack);
        }

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (stack.getTagCompound().hasKey(NBT_STORAGE_TRACKER)) {
            storageTracker.readFromNbt(stack.getTagCompound().getTagList(NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }

        if (stack.getTagCompound().hasKey(NBT_FLUID_STORAGE_TRACKER)) {
            fluidStorageTracker.readFromNbt(stack.getTagCompound().getTagList(NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }

        StackUtils.readItems(disk, 4, stack.getTagCompound());

        if (!player.getEntityWorld().isRemote) {
            API.instance().getOneSixMigrationHelper().migrateDiskInventory(player.getEntityWorld(), disk);
        }

        StackUtils.readItems(filter, 0, stack.getTagCompound());

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
        if (RS.INSTANCE.config.portableGridUsesEnergy && stack.getItemDamage() != ItemBlockPortableGrid.TYPE_CREATIVE) {
            stack.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(energy, false);
        }
    }

    @Override
    public int getEnergy() {
        if (RS.INSTANCE.config.portableGridUsesEnergy && stack.getItemDamage() != ItemBlockPortableGrid.TYPE_CREATIVE) {
            return stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
        }

        return RS.INSTANCE.config.portableGridCapacity;
    }

    @Override
    public ItemHandlerBase getDisk() {
        return disk;
    }

    public EntityPlayer getPlayer() {
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
    public IStorageCacheListener createListener(EntityPlayerMP player) {
        return getGridType() == GridType.FLUID ? new StorageCacheListenerGridPortableFluid(this, player) : new StorageCacheListenerGridPortable(this, player);
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
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), type, getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingType = type;

        GuiBase.executeLater(GuiGrid.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), direction, getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingDirection = direction;

        GuiBase.executeLater(GuiGrid.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), searchBoxMode, getSize(), getTabSelected(), getTabPage()));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void onSizeChanged(int size) {
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), size, getTabSelected(), getTabPage()));

        this.size = size;

        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().currentScreen.initGui();
        }
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        this.tabSelected = tab == tabSelected ? -1 : tab;

        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), tabSelected, getTabPage()));

        GuiBase.executeLater(GuiGrid.class, grid -> grid.getView().sort());
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0 && page <= getTotalTabPages()) {
            RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), page));

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
    public StorageTrackerItem getItemStorageTracker() {
        return storageTracker;
    }

    @Override
    public StorageTrackerFluid getFluidStorageTracker() {
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
        if (!player.getEntityWorld().isRemote) {
            StackUtils.writeItems(disk, 4, stack.getTagCompound());
        }
    }

    @Override
    public boolean isActive() {
        if (RS.INSTANCE.config.portableGridUsesEnergy && stack.getItemDamage() != ItemBlockPortableGrid.TYPE_CREATIVE && stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() <= RS.INSTANCE.config.portableGridOpenUsage) {
            return false;
        }

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
