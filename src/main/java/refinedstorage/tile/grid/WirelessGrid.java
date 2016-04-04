package refinedstorage.tile.grid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.block.EnumGridType;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.network.MessageWirelessGridSettingsUpdate;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.util.HandUtils;

import java.util.List;

// @TODO: Fix this as well
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
    public List<ItemGroup> getItemGroups() {
        return null;
    }

    @Override
    public void onItemPush(int playerSlot, boolean one) {

    }

    @Override
    public void onItemPull(int id, int flags) {

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
        RefinedStorage.NETWORK.sendToServer(new MessageWirelessGridSettingsUpdate(HandUtils.getIdFromHand(hand), getSortingDirection(), type, getSearchBoxMode()));

        this.sortingType = type;
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RefinedStorage.NETWORK.sendToServer(new MessageWirelessGridSettingsUpdate(HandUtils.getIdFromHand(hand), direction, getSortingType(), getSearchBoxMode()));

        this.sortingDirection = direction;
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RefinedStorage.NETWORK.sendToServer(new MessageWirelessGridSettingsUpdate(HandUtils.getIdFromHand(hand), getSortingDirection(), getSortingType(), searchBoxMode));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public IRedstoneModeConfig getRedstoneModeSetting() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return false;
    }
}
