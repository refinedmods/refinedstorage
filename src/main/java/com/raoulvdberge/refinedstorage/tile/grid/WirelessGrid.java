package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.*;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.listener.ItemGridStorageCacheListener;
import com.raoulvdberge.refinedstorage.inventory.item.FilterItemHandler;
import com.raoulvdberge.refinedstorage.item.WirelessGridItem;
import com.raoulvdberge.refinedstorage.network.grid.WirelessGridSettingsUpdateMessage;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WirelessGrid implements INetworkAwareGrid {
    private ItemStack stack;
    @Nullable
    private final MinecraftServer server;
    private final DimensionType nodeDimension;
    private final BlockPos nodePos;
    private final int slotId;

    private int viewType;
    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;

    private List<IFilter> filters = new ArrayList<>();
    private List<IGridTab> tabs = new ArrayList<>();
    private FilterItemHandler filter = (FilterItemHandler) new FilterItemHandler(filters, tabs)
        .addListener((handler, slot, reading) -> {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundNBT());
            }

            StackUtils.writeItems(handler, 0, stack.getTag());
        });

    public WirelessGrid(ItemStack stack, @Nullable MinecraftServer server, int slotId) {
        this.stack = stack;
        this.server = server;
        this.nodeDimension = WirelessGridItem.getDimension(stack);
        this.nodePos = new BlockPos(WirelessGridItem.getX(stack), WirelessGridItem.getY(stack), WirelessGridItem.getZ(stack));
        this.slotId = slotId;

        this.viewType = WirelessGridItem.getViewType(stack);
        this.sortingType = WirelessGridItem.getSortingType(stack);
        this.sortingDirection = WirelessGridItem.getSortingDirection(stack);
        this.searchBoxMode = WirelessGridItem.getSearchBoxMode(stack);
        this.tabSelected = WirelessGridItem.getTabSelected(stack);
        this.tabPage = WirelessGridItem.getTabPage(stack);
        this.size = WirelessGridItem.getSize(stack);

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
        World world = DimensionManager.getWorld(server, nodeDimension, true, true);

        if (world != null) {
            return NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(world.getTileEntity(nodePos)));
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
        RS.NETWORK_HANDLER.sendToServer(new WirelessGridSettingsUpdateMessage(type, getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.viewType = type;

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSortingTypeChanged(int type) {
        RS.NETWORK_HANDLER.sendToServer(new WirelessGridSettingsUpdateMessage(getViewType(), getSortingDirection(), type, getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingType = type;

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RS.NETWORK_HANDLER.sendToServer(new WirelessGridSettingsUpdateMessage(getViewType(), direction, getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingDirection = direction;

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RS.NETWORK_HANDLER.sendToServer(new WirelessGridSettingsUpdateMessage(getViewType(), getSortingDirection(), getSortingType(), searchBoxMode, getSize(), getTabSelected(), getTabPage()));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void onSizeChanged(int size) {
        RS.NETWORK_HANDLER.sendToServer(new WirelessGridSettingsUpdateMessage(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), size, getTabSelected(), getTabPage()));

        this.size = size;

        BaseScreen.executeLater(GridScreen.class, BaseScreen::init);
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        this.tabSelected = tab == tabSelected ? -1 : tab;

        RS.NETWORK_HANDLER.sendToServer(new WirelessGridSettingsUpdateMessage(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), tabSelected, getTabPage()));

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0 && page <= getTotalTabPages()) {
            RS.NETWORK_HANDLER.sendToServer(new WirelessGridSettingsUpdateMessage(getViewType(), getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), page));

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
    public boolean isGridActive() {
        return true;
    }

    @Override
    public int getSlotId() {
        return slotId;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        INetwork network = getNetwork();

        if (network != null) {
            network.getNetworkItemManager().close(player);
        }
    }
}
