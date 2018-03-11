package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheListenerGridFluid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.item.ItemWirelessFluidGrid;
import com.raoulvdberge.refinedstorage.network.MessageWirelessFluidGridSettingsUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class WirelessFluidGrid implements IGrid {
    public static int ID;

    private ItemStack stack;

    private int networkDimension;
    private BlockPos network;

    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int size;

    public WirelessFluidGrid(int networkDimension, ItemStack stack) {
        this.networkDimension = networkDimension;
        this.network = new BlockPos(ItemWirelessFluidGrid.getX(stack), ItemWirelessFluidGrid.getY(stack), ItemWirelessFluidGrid.getZ(stack));

        this.stack = stack;

        this.sortingType = ItemWirelessFluidGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessFluidGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessFluidGrid.getSearchBoxMode(stack);
        this.size = ItemWirelessFluidGrid.getSize(stack);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public GridType getType() {
        return GridType.FLUID;
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
    public IStorageCacheListener createListener(EntityPlayerMP player) {
        return new StorageCacheListenerGridFluid(player, getNetwork());
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:fluid_grid";
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
        return 0;
    }

    @Override
    public int getTabPage() {
        return 0;
    }

    @Override
    public int getTotalTabPages() {
        return 0;
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
        RS.INSTANCE.network.sendToServer(new MessageWirelessFluidGridSettingsUpdate(getSortingDirection(), type, getSearchBoxMode(), getSize()));

        this.sortingType = type;

        GuiBase.executeLater(GuiGrid.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessFluidGridSettingsUpdate(direction, getSortingType(), getSearchBoxMode(), getSize()));

        this.sortingDirection = direction;

        GuiBase.executeLater(GuiGrid.class, grid -> grid.getView().sort());
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessFluidGridSettingsUpdate(getSortingDirection(), getSortingType(), searchBoxMode, getSize()));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void onSizeChanged(int size) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessFluidGridSettingsUpdate(getSortingDirection(), getSortingType(), getSearchBoxMode(), size));

        this.size = size;

        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().currentScreen.initGui();
        }
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        // NO OP
    }

    @Override
    public void onTabPageChanged(int page) {
        // NO OP
    }

    @Override
    public List<IFilter> getFilters() {
        return Collections.emptyList();
    }

    @Override
    public List<IGridTab> getTabs() {
        return Collections.emptyList();
    }

    @Override
    public IItemHandlerModifiable getFilter() {
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
