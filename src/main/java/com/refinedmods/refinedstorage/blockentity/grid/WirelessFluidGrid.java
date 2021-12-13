package com.refinedmods.refinedstorage.blockentity.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.grid.*;
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.FluidGridStorageCacheListener;
import com.refinedmods.refinedstorage.inventory.item.FilterItemHandler;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.item.NetworkItem;
import com.refinedmods.refinedstorage.item.WirelessFluidGridItem;
import com.refinedmods.refinedstorage.network.grid.WirelessFluidGridSettingsUpdateMessage;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WirelessFluidGrid implements INetworkAwareGrid {
    @Nullable
    private final MinecraftServer server;
    private final ResourceKey<Level> nodeDimension;
    private final BlockPos nodePos;
    private final PlayerSlot slot;
    private final List<IFilter> filters = new ArrayList<>();
    private final List<IGridTab> tabs = new ArrayList<>();
    private ItemStack stack;
    private final FilterItemHandler filter = (FilterItemHandler) new FilterItemHandler(filters, tabs)
        .addListener(((handler, slot, reading) -> {
            if (!stack.hasTag()) {
                stack.setTag(new CompoundTag());
            }

            StackUtils.writeItems(handler, 0, stack.getTag());
        }));
    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;
    private int tabPage;
    private int size;

    public WirelessFluidGrid(ItemStack stack, @Nullable MinecraftServer server, PlayerSlot slot) {
        this.stack = stack;
        this.server = server;
        this.nodeDimension = NetworkItem.getDimension(stack);
        this.nodePos = new BlockPos(NetworkItem.getX(stack), NetworkItem.getY(stack), NetworkItem.getZ(stack));
        this.slot = slot;

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
        Level level = server.getLevel(nodeDimension);
        if (level != null) {
            return NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromBlockEntity(level.getBlockEntity(nodePos)));
        }
        return null;
    }

    @Override
    public IStorageCacheListener createListener(ServerPlayer player) {
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
    public Component getTitle() {
        return new TranslatableComponent("gui.refinedstorage.fluid_grid");
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
    public boolean isGridActive() {
        return true;
    }

    @Override
    public int getSlotId() {
        return slot.getSlotIdInPlayerInventory();
    }

    @Override
    public void onClosed(Player player) {
        INetwork network = getNetwork();

        if (network != null) {
            network.getNetworkItemManager().close(player);
        }
    }
}
