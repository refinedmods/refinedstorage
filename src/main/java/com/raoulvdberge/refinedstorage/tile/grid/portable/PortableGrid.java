package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.storage.StorageDiskType;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.ItemGridHandlerPortable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.NetworkNodeDiskDrive;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheItemPortable;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageDiskItemPortable;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.item.ItemBlockPortableGrid;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import com.raoulvdberge.refinedstorage.item.filter.Filter;
import com.raoulvdberge.refinedstorage.item.filter.FilterTab;
import com.raoulvdberge.refinedstorage.network.MessageGridSettingsUpdate;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PortableGrid implements IGrid, IPortableGrid {
    public static final int GRID_TYPE = 2;

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
    private int size;

    private List<Filter> filters = new ArrayList<>();
    private List<FilterTab> tabs = new ArrayList<>();
    private ItemHandlerFilter filter = new ItemHandlerFilter(filters, tabs, null) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            RSUtils.writeItems(this, slot, stack.getTagCompound());
        }
    };
    private ItemHandlerBase disk = new ItemHandlerBase(1, s -> NetworkNodeDiskDrive.VALIDATOR_STORAGE_DISK.test(s) && ((IStorageDiskProvider) s.getItem()).create(s).getType() == StorageDiskType.ITEMS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                if (getStackInSlot(slot).isEmpty()) {
                    storage = null;
                } else {
                    IStorageDiskProvider provider = (IStorageDiskProvider) getStackInSlot(slot).getItem();

                    storage = new StorageDiskItemPortable(provider.create(getStackInSlot(slot)), PortableGrid.this);
                    storage.readFromNBT();
                    storage.onPassContainerContext(() -> {
                    }, () -> false, () -> AccessType.INSERT_EXTRACT);
                }

                cache.invalidate();

                RSUtils.writeItems(this, 4, stack.getTagCompound());
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

    public PortableGrid(EntityPlayer player, ItemStack stack) {
        this.player = player;
        this.stack = stack;

        // Only extract when we can
        if (isActive()) {
            drainEnergy(RS.INSTANCE.config.portableGridOpenUsage);
        }

        this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
        this.tabSelected = ItemWirelessGrid.getTabSelected(stack);
        this.size = ItemWirelessGrid.getSize(stack);

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        for (int i = 0; i < 4; ++i) {
            RSUtils.readItems(filter, i, stack.getTagCompound());
        }

        RSUtils.readItems(disk, 4, stack.getTagCompound());
    }

    public ItemStack getStack() {
        return stack;
    }

    public StorageCacheItemPortable getCache() {
        return cache;
    }

    @Nullable
    public IStorageDisk<ItemStack> getStorage() {
        return storage;
    }

    @Override
    public List<EntityPlayer> getWatchers() {
        return Collections.singletonList(player);
    }

    @Override
    public void drainEnergy(int energy) {
        if (RS.INSTANCE.config.portableGridUsesEnergy && stack.getItemDamage() != ItemBlockPortableGrid.TYPE_CREATIVE) {
            stack.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(energy, false);
        }
    }

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
    public int getSize() {
        return size;
    }

    @Override
    public void onViewTypeChanged(int type) {
        // NO OP
    }

    @Override
    public void onSortingTypeChanged(int type) {
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), type, getSearchBoxMode(), getSize(), getTabSelected()));

        this.sortingType = type;

        GuiGrid.markForSorting();
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), direction, getSortingType(), getSearchBoxMode(), getSize(), getTabSelected()));

        this.sortingDirection = direction;

        GuiGrid.markForSorting();
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), searchBoxMode, getSize(), getTabSelected()));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void onSizeChanged(int size) {
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), size, getTabSelected()));

        this.size = size;

        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().currentScreen.initGui();
        }
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        this.tabSelected = tab == tabSelected ? -1 : tab;

        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), tabSelected));

        GuiGrid.markForSorting();
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
        return null;
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
        if (!player.world.isRemote && storage != null) {
            storage.writeToNBT();

            RSUtils.writeItems(disk, 4, stack.getTagCompound());
        }
    }

    @Override
    public boolean isActive() {
        if (RS.INSTANCE.config.portableGridUsesEnergy && stack.getItemDamage() != ItemBlockPortableGrid.TYPE_CREATIVE && stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() <= RS.INSTANCE.config.portableGridOpenUsage) {
            return false;
        }

        return true;
    }
}
