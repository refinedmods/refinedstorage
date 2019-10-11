package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.*;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.listener.ItemGridStorageCacheListener;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WirelessGrid implements IGridNetworkAware {
    public static int ID;

    private ItemStack stack;

    private int networkDimension;
    private BlockPos network;

    private int viewType;
    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;

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

    public WirelessGrid(ItemStack stack) {
        /* TODO this.networkDimension = ItemWirelessGrid.getDimensionId(stack);
        this.network = new BlockPos(ItemWirelessGrid.getX(stack), ItemWirelessGrid.getY(stack), ItemWirelessGrid.getZ(stack));

        this.stack = stack;

        this.viewType = ItemWirelessGrid.getViewType(stack);
        this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
        this.tabSelected = ItemWirelessGrid.getTabSelected(stack);
        this.tabPage = ItemWirelessGrid.getTabPage(stack);
        this.size = ItemWirelessGrid.getSize(stack);*/

        if (stack.hasTag()) {
            StackUtils.readItems(filter, 0, stack.getTag());
        }
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public GridType getGridType() {
        return GridType.NORMAL;
    }

    @Override
    @Nullable
    public INetwork getNetwork() {
        // TODO World world = DimensionManager.getWorld(networkDimension);
        World world = null;

        if (world != null) {
            TileEntity tile = world.getTileEntity(network);

            return tile instanceof INetwork ? (INetwork) tile : null;
        }

        return null;
    }

    @Override
    public IStorageCacheListener createListener(ServerPlayerEntity player) {
        return new ItemGridStorageCacheListener(player, getNetwork());
    }

    @Nullable
    @Override
    public IStorageCache getStorageCache() {
        INetwork network = getNetwork();

        return network != null ? network.getItemStorageCache() : null;
    }

    @Nullable
    @Override
    public IItemGridHandler getItemHandler() {
        INetwork network = getNetwork();

        return network != null ? network.getItemGridHandler() : null;
    }

    @Nullable
    @Override
    public IFluidGridHandler getFluidHandler() {
        return null;
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
        return new TranslationTextComponent("gui.refinedstorage.grid");
    }

    @Override
    public int getViewType() {
        return viewType;
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
        // TODO RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(type, getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.viewType = type;

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
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
    public boolean isActive() {
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        INetwork network = getNetwork();

        if (network != null) {
            network.getNetworkItemHandler().close(player);
        }
    }
}
