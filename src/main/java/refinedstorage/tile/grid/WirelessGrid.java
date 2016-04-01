package refinedstorage.tile.grid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.block.EnumGridType;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.network.MessageWirelessGridSettingsUpdate;
import refinedstorage.tile.TileController;
import refinedstorage.tile.settings.IRedstoneModeSetting;

public class WirelessGrid implements IGrid {
    private ItemStack stack;
    private EnumHand hand;
    private World world;
    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;

    public WirelessGrid(ItemStack stack, EnumHand hand, World world) {
        this.stack = stack;
        this.hand = hand;
        this.world = world;
        this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
    }

    @Override
    public EnumGridType getType() {
        return EnumGridType.NORMAL;
    }

    @Override
    public TileController getController() {
        return (TileController) world.getTileEntity(new BlockPos(ItemWirelessGrid.getX(stack), ItemWirelessGrid.getY(stack), ItemWirelessGrid.getZ(stack)));
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
    public void onSortingTypeChanged(int type) {
        RefinedStorage.NETWORK.sendToServer(new MessageWirelessGridSettingsUpdate(hand == EnumHand.OFF_HAND ? 1 : 0, getSortingDirection(), type, getSearchBoxMode()));

        this.sortingType = type;
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RefinedStorage.NETWORK.sendToServer(new MessageWirelessGridSettingsUpdate(hand == EnumHand.OFF_HAND ? 1 : 0, direction, getSortingType(), getSearchBoxMode()));

        this.sortingDirection = direction;
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RefinedStorage.NETWORK.sendToServer(new MessageWirelessGridSettingsUpdate(hand == EnumHand.OFF_HAND ? 1 : 0, getSortingDirection(), getSortingType(), searchBoxMode));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public IRedstoneModeSetting getRedstoneModeSetting() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return getController() instanceof TileController && getController().isActiveClientSide();
    }

    @Override
    public boolean isWireless() {
        return true;
    }
}
