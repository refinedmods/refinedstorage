package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilteredItem;
import com.raoulvdberge.refinedstorage.gui.grid.GridTab;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerGridFilterInGrid;
import com.raoulvdberge.refinedstorage.item.ItemWirelessGrid;
import com.raoulvdberge.refinedstorage.network.MessageWirelessGridSettingsUpdate;
import com.raoulvdberge.refinedstorage.tile.TileController;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

public class WirelessGrid implements IGrid {
    private ItemStack stack;

    private int controllerDimension;
    private BlockPos controller;

    private int viewType;
    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private int tabSelected;

    private List<GridFilteredItem> filteredItems = new ArrayList<>();
    private List<GridTab> tabs = new ArrayList<>();
    private ItemHandlerGridFilterInGrid filter = new ItemHandlerGridFilterInGrid(filteredItems, tabs) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
                if (!stack.hasTagCompound()) {
                    stack.setTagCompound(new NBTTagCompound());
                }

                RSUtils.writeItems(this, slot, stack.getTagCompound());
            }
        }
    };

    public WirelessGrid(int controllerDimension, ItemStack stack) {
        this.controllerDimension = controllerDimension;
        this.controller = new BlockPos(ItemWirelessGrid.getX(stack), ItemWirelessGrid.getY(stack), ItemWirelessGrid.getZ(stack));

        this.stack = stack;

        this.viewType = ItemWirelessGrid.getViewType(stack);
        this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
        this.tabSelected = ItemWirelessGrid.getTabSelected(stack);

        if (stack.hasTagCompound()) {
            for (int i = 0; i < 4; ++i) {
                RSUtils.readItems(filter, i, stack.getTagCompound());
            }
        }
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public EnumGridType getType() {
        return EnumGridType.NORMAL;
    }

    @Override
    public BlockPos getNetworkPosition() {
        return controller;
    }

    @Override
    public IItemGridHandler getItemHandler() {
        TileController controller = getController();

        return controller != null ? controller.getItemGridHandler() : null;
    }

    @Override
    public IFluidGridHandler getFluidHandler() {
        return null;
    }

    @Override
    public String getGuiTitle() {
        return "gui.refinedstorage:wireless_grid";
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
    public void onViewTypeChanged(int type) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessGridSettingsUpdate(type, getSortingDirection(), getSortingType(), getSearchBoxMode(), tabSelected));

        this.viewType = type;

        GuiGrid.markForSorting();
    }

    @Override
    public void onSortingTypeChanged(int type) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessGridSettingsUpdate(getViewType(), getSortingDirection(), type, getSearchBoxMode(), tabSelected));

        this.sortingType = type;

        GuiGrid.markForSorting();
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessGridSettingsUpdate(getViewType(), direction, getSortingType(), getSearchBoxMode(), tabSelected));

        this.sortingDirection = direction;

        GuiGrid.markForSorting();
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), searchBoxMode, tabSelected));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        RS.INSTANCE.network.sendToServer(new MessageWirelessGridSettingsUpdate(getViewType(), getSortingDirection(), getSortingType(), searchBoxMode, tab));

        this.tabSelected = tab;

        GuiGrid.markForSorting();
    }

    @Override
    public List<GridFilteredItem> getFilteredItems() {
        return filteredItems;
    }

    @Override
    public List<GridTab> getTabs() {
        return tabs;
    }

    @Override
    public ItemHandlerBasic getFilter() {
        return filter;
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeConfig() {
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    public void onClose(EntityPlayer player) {
        TileController controller = getController();

        if (controller != null) {
            controller.getNetworkItemHandler().onClose(player);
        }
    }

    private TileController getController() {
        World world = DimensionManager.getWorld(controllerDimension);

        if (world != null) {
            TileEntity tile = world.getTileEntity(controller);

            return tile instanceof TileController ? (TileController) tile : null;
        }

        return null;
    }
}
