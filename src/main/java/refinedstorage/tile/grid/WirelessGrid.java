package refinedstorage.tile.grid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.block.EnumGridType;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.network.MessageWirelessGridSettingsUpdate;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.controller.TileController;

public class WirelessGrid implements IGrid {
    private EnumHand hand;
    private World world;
    private BlockPos controllerPos;
    private int viewType;
    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;

    public WirelessGrid(World world, ItemStack stack, EnumHand hand) {
        this.hand = hand;
        this.world = world;
        this.controllerPos = new BlockPos(ItemWirelessGrid.getX(stack), ItemWirelessGrid.getY(stack), ItemWirelessGrid.getZ(stack));
        this.viewType = ItemWirelessGrid.getViewType(stack);
        this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
    }

    @Override
    public EnumGridType getType() {
        return EnumGridType.NORMAL;
    }

    @Override
    public BlockPos getNetworkPosition() {
        return controllerPos;
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
        TileEntity tile = world.getTileEntity(controllerPos);

        return tile instanceof TileController ? (TileController) tile : null;
    }
}
