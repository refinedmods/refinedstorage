package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.*;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.ItemGridHandlerPortable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.NetworkNodeDiskDrive;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheItemPortable;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheListenerGridPortable;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageDiskItemPortable;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageTrackerItem;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.item.ItemBlockPortableGrid;
import com.raoulvdberge.refinedstorage.item.ItemEnergyItem;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PortableGrid implements IGrid, IPortableGrid {
    public static int ID;

    public static final String NBT_STORAGE_TRACKER = "StorageTracker";

    @Nullable
    private IStorageDisk<ItemStack> storage;
    private StorageCacheItemPortable cache = new StorageCacheItemPortable(this);
    private ItemGridHandlerPortable handler = new ItemGridHandlerPortable(this, this);

    private EntityPlayer player;
    private ItemStack stack;

    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;

    private StorageTrackerItem storageTracker = new StorageTrackerItem(() -> {
        stack.getTagCompound().setTag(NBT_STORAGE_TRACKER, getStorageTracker().serializeNBT());
    });

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
    private ItemHandlerBase disk = new ItemHandlerBase(1, s -> NetworkNodeDiskDrive.VALIDATOR_STORAGE_DISK.test(s) && ((IStorageDiskProvider) s.getItem()).create(s).getType() == StorageDiskType.ITEMS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER || (player == null && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)) {
                if (getStackInSlot(slot).isEmpty()) {
                    storage = null;
                } else {
                    IStorageDiskProvider provider = (IStorageDiskProvider) getStackInSlot(slot).getItem();

                    storage = new StorageDiskItemPortable(provider.create(getStackInSlot(slot)), PortableGrid.this);

                    if (player != null) {
                        storage.readFromNBT();
                        storage.onPassContainerContext(() -> {
                        }, () -> false, () -> AccessType.INSERT_EXTRACT);
                    }
                }

                if (player != null) {
                    cache.invalidate();

                    StackUtils.writeItems(this, 4, stack.getTagCompound());
                }
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

    public PortableGrid(@Nullable EntityPlayer player, ItemStack stack) {
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
            storageTracker.readFromNBT(stack.getTagCompound().getTagList(NBT_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }

        if (player != null) {
            StackUtils.readItems(filter, 0, stack.getTagCompound());
        }

        StackUtils.readItems(disk, 4, stack.getTagCompound());

        if (player != null) {
            drainEnergy(RS.INSTANCE.config.portableGridOpenUsage);

            // If there is no disk onContentsChanged isn't called and the update isn't sent, thus items from the previous grid view would remain clientside
            if (!player.getEntityWorld().isRemote && disk.getStackInSlot(0).isEmpty()) {
                cache.invalidate();
            }
        }
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public StorageCacheItemPortable getCache() {
        return cache;
    }

    @Override
    @Nullable
    public IStorageDisk<ItemStack> getStorage() {
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

        return ItemEnergyItem.CAPACITY;
    }

    @Override
    public ItemHandlerBase getDisk() {
        return disk;
    }

    public EntityPlayer getPlayer() {
        return player;
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
        if (!player.getEntityWorld().isRemote && storage != null) {
            storage.writeToNBT();

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
}
