package refinedstorage.tile.grid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.block.EnumGridType;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerGridFilterInGrid;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.network.MessageWirelessGridSettingsUpdate;
import refinedstorage.tile.TileBase;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.controller.TileController;

import java.util.ArrayList;
import java.util.List;

public class WirelessGrid implements IGrid {
    private World world;

    private EnumHand hand;
    private ItemStack stack;

    private BlockPos controller;

    private int viewType;
    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;

    private List<ItemStack> filteredItems = new ArrayList<ItemStack>();
    private ItemHandlerGridFilterInGrid filter = new ItemHandlerGridFilterInGrid(filteredItems) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!world.isRemote) {
                if (!stack.hasTagCompound()) {
                    stack.setTagCompound(new NBTTagCompound());
                }

                TileBase.writeItems(this, slot, stack.getTagCompound());
            }
        }
    };

    public WirelessGrid(World world, ItemStack stack, EnumHand hand) {
        this.world = world;

        this.stack = stack;
        this.hand = hand;

        this.controller = new BlockPos(ItemWirelessGrid.getX(stack), ItemWirelessGrid.getY(stack), ItemWirelessGrid.getZ(stack));

        this.viewType = ItemWirelessGrid.getViewType(stack);
        this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);

        if (stack.hasTagCompound()) {
            for (int i = 0; i < 4; ++i) {
                TileBase.readItems(filter, i, stack.getTagCompound());
            }
        }
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
    public IGridHandler getGridHandler() {
        TileController controller = getController();

        return controller != null ? controller.getGridHandler() : null;
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
    public void onViewTypeChanged(int type) {
        RefinedStorage.INSTANCE.network.sendToServer(new MessageWirelessGridSettingsUpdate(hand.ordinal(), type, getSortingDirection(), getSortingType(), getSearchBoxMode()));

        this.viewType = type;
    }

    @Override
    public void onSortingTypeChanged(int type) {
        RefinedStorage.INSTANCE.network.sendToServer(new MessageWirelessGridSettingsUpdate(hand.ordinal(), getViewType(), getSortingDirection(), type, getSearchBoxMode()));

        this.sortingType = type;
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RefinedStorage.INSTANCE.network.sendToServer(new MessageWirelessGridSettingsUpdate(hand.ordinal(), getViewType(), direction, getSortingType(), getSearchBoxMode()));

        this.sortingDirection = direction;
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RefinedStorage.INSTANCE.network.sendToServer(new MessageWirelessGridSettingsUpdate(hand.ordinal(), getViewType(), getSortingDirection(), getSortingType(), searchBoxMode));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public List<ItemStack> getFilteredItems() {
        return filteredItems;
    }

    @Override
    public ItemHandlerBasic getFilter() {
        return filter;
    }

    @Override
    public IRedstoneModeConfig getRedstoneModeConfig() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    public void onClose(EntityPlayer player) {
        TileController controller = getController();

        if (controller != null) {
            controller.getWirelessGridHandler().onClose(player);
        }
    }

    private TileController getController() {
        TileEntity tile = world.getTileEntity(controller);

        return tile instanceof TileController ? (TileController) tile : null;
    }
}
