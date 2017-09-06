package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFilter;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import com.raoulvdberge.refinedstorage.network.MessageGridSettingsUpdate;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WirelessGrid implements IGrid {
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

            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            StackUtils.writeItems(this, 0, stack.getTagCompound());
        }
    };

    public WirelessGrid(int networkDimension, ItemStack stack) {
        this.networkDimension = networkDimension;
        this.network = new BlockPos(ItemWirelessGrid.getX(stack), ItemWirelessGrid.getY(stack), ItemWirelessGrid.getZ(stack));

        this.stack = stack;

        this.viewType = ItemWirelessGrid.getViewType(stack);
        this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
        this.tabSelected = ItemWirelessGrid.getTabSelected(stack);
        this.tabPage = ItemWirelessGrid.getTabPage(stack);
        this.size = ItemWirelessGrid.getSize(stack);

        if (stack.hasTagCompound()) {
            StackUtils.readItems(filter, 0, stack.getTagCompound());
        }
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public GridType getType() {
        return GridType.NORMAL;
    }

    @Override
    @Nullable
    public INetwork getNetwork() {
        World world = DimensionManager.getWorld(networkDimension);

        if (world != null) {
            TileEntity tile = world.getTileEntity(network);

            return tile instanceof INetwork ? (INetwork) tile : null;
        }

        return null;
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:grid";
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
        return tabPage;
    }

    @Override
    public int getTotalTabPages() {
        return (int) Math.floor((float) tabs.size() / (float) IGrid.TABS_PER_PAGE);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void onViewTypeChanged(int type) {
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(type, getSortingDirection(), getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.viewType = type;

        GuiGrid.markForSorting();
    }

    @Override
    public void onSortingTypeChanged(int type) {
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), getSortingDirection(), type, getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingType = type;

        GuiGrid.markForSorting();
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RS.INSTANCE.network.sendToServer(new MessageGridSettingsUpdate(getViewType(), direction, getSortingType(), getSearchBoxMode(), getSize(), getTabSelected(), getTabPage()));

        this.sortingDirection = direction;

        GuiGrid.markForSorting();
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

        GuiGrid.markForSorting();
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
    public boolean isActive() {
        return true;
    }

    @Override
    public void onClosed(EntityPlayer player) {
        INetwork network = getNetwork();

        if (network != null) {
            network.getNetworkItemHandler().onClose(player);
        }
    }
}
