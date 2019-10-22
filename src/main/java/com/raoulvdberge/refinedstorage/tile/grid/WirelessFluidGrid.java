package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.*;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.listener.FluidGridStorageCacheListener;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import com.raoulvdberge.refinedstorage.inventory.item.FilterItemHandler;
import com.raoulvdberge.refinedstorage.item.WirelessFluidGridItem;
import com.raoulvdberge.refinedstorage.network.grid.WirelessFluidGridSettingsUpdateMessage;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import com.raoulvdberge.refinedstorage.screen.grid.GridScreen;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
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

public class WirelessFluidGrid implements INetworkAwareGrid {
    private ItemStack stack;
    @Nullable
    private final MinecraftServer server;
    private final DimensionType nodeDimension;
    private final BlockPos nodePos;

    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;

    private List<IFilter> filters = new ArrayList<>();
    private List<IGridTab> tabs = new ArrayList<>();
    private FilterItemHandler filter = (FilterItemHandler) new FilterItemHandler(filters, tabs)
        .addListener(((handler, slot, reading) -> {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundNBT());
            }

            StackUtils.writeItems(handler, 0, stack.getTag());
        }));

    public WirelessFluidGrid(ItemStack stack, @Nullable MinecraftServer server) {
        this.stack = stack;
        this.server = server;
        this.nodeDimension = WirelessFluidGridItem.getDimension(stack);
        this.nodePos = new BlockPos(WirelessFluidGridItem.getX(stack), WirelessFluidGridItem.getY(stack), WirelessFluidGridItem.getZ(stack));

        this.sortingType = WirelessFluidGridItem.getSortingType(stack);
        this.sortingDirection = WirelessFluidGridItem.getSortingDirection(stack);
        this.searchBoxMode = WirelessFluidGridItem.getSearchBoxMode(stack);
        this.tabSelected = WirelessFluidGridItem.getTabSelected(stack);
        this.tabPage = WirelessFluidGridItem.getTabPage(stack);
        this.size = WirelessFluidGridItem.getSize(stack);

        if (stack.hasTag()) {
            StackUtils.readItems(filter, 0, stack.getTag());
        }
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public GridType getGridType() {
        return GridType.FLUID;
    }

    @Override
    @Nullable
    public INetwork getNetwork() {
        World world = DimensionManager.getWorld(server, nodeDimension, true, true);

        if (world != null) {
            TileEntity tile = world.getTileEntity(nodePos);

            if (tile != null) {
                INetworkNodeProxy proxy = tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY).orElse(null);

                if (proxy != null) {
                    return proxy.getNode().getNetwork();
                }
            }
        }

        return null;
    }

    @Override
    public IStorageCacheListener createListener(ServerPlayerEntity player) {
        return new FluidGridStorageCacheListener(player, getNetwork());
    }

    @Nullable
    @Override
    public IStorageCache getStorageCache() {
        INetwork network = getNetwork();

        return network != null ? network.getFluidStorageCache() : null;
    }

    @Nullable
    @Override
    public IItemGridHandler getItemHandler() {
        return null;
    }

    @Nullable
    @Override
    public IFluidGridHandler getFluidHandler() {
        INetwork network = getNetwork();

        return network != null ? network.getFluidGridHandler() : null;
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
        return new TranslationTextComponent("gui.refinedstorage.fluid_grid");
    }

    @Override
    public int getViewType() {
        return 0;
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
        RS.NETWORK_HANDLER.sendToServer(new WirelessFluidGridSettingsUpdateMessage(getSortingDirection(), type, getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingType = type;

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RS.NETWORK_HANDLER.sendToServer(new WirelessFluidGridSettingsUpdateMessage(direction, getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingDirection = direction;

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RS.NETWORK_HANDLER.sendToServer(new WirelessFluidGridSettingsUpdateMessage(getSortingDirection(), getSortingType(), searchBoxMode, getSize(), getTabSelected(), getTabPage()));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void onSizeChanged(int size) {
        RS.NETWORK_HANDLER.sendToServer(new WirelessFluidGridSettingsUpdateMessage(getSortingDirection(), getSortingType(), getSearchBoxMode(), size, getTabSelected(), getTabPage()));

        this.size = size;

        BaseScreen.executeLater(GridScreen.class, BaseScreen::init);
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        this.tabSelected = tab == tabSelected ? -1 : tab;

        RS.NETWORK_HANDLER.sendToServer(new WirelessFluidGridSettingsUpdateMessage(getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), tabSelected, getTabPage()));

        BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0 && page <= getTotalTabPages()) {
            RS.NETWORK_HANDLER.sendToServer(new WirelessFluidGridSettingsUpdateMessage(getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), page));

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
            network.getNetworkItemManager().close(player);
        }
    }
}
