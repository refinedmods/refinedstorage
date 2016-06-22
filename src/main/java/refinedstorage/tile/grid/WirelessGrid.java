package refinedstorage.tile.grid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.apiimpl.network.NetworkMasterRegistry;
import refinedstorage.block.EnumGridType;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.network.MessageWirelessGridSettingsUpdate;
import refinedstorage.tile.config.IRedstoneModeConfig;

import java.util.ArrayList;
import java.util.List;

public class WirelessGrid implements IGrid {
    private EnumHand hand;
    private World world;
    private BlockPos controllerPos;
    private int sortingType;
    private int sortingDirection;
    private int searchBoxMode;
    private List<ItemStack> items = new ArrayList<ItemStack>();
    private long lastUpdate;

    public WirelessGrid(World world, ItemStack stack, EnumHand hand) {
        this.hand = hand;
        this.world = world;
        this.controllerPos = new BlockPos(ItemWirelessGrid.getX(stack), ItemWirelessGrid.getY(stack), ItemWirelessGrid.getZ(stack));
        this.sortingType = ItemWirelessGrid.getSortingType(stack);
        this.sortingDirection = ItemWirelessGrid.getSortingDirection(stack);
        this.searchBoxMode = ItemWirelessGrid.getSearchBoxMode(stack);
    }

    @Override
    public EnumGridType getType() {
        return EnumGridType.NORMAL;
    }

    @Override
    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setItems(List<ItemStack> items) {
        this.items = items;
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public BlockPos getNetworkPosition() {
        return controllerPos;
    }

    @Override
    public IGridHandler getGridHandler() {
        INetworkMaster network = NetworkMasterRegistry.get(world, controllerPos);

        if (network != null) {
            return network.getGridHandler();
        }

        return null;
    }

    public void onClose(EntityPlayer player) {
        INetworkMaster network = NetworkMasterRegistry.get(world, controllerPos);

        if (network != null) {
            network.getWirelessGridHandler().onClose(player);
        }
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
        RefinedStorage.NETWORK.sendToServer(new MessageWirelessGridSettingsUpdate(RefinedStorageUtils.getIdFromHand(hand), getSortingDirection(), type, getSearchBoxMode()));

        this.sortingType = type;
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        RefinedStorage.NETWORK.sendToServer(new MessageWirelessGridSettingsUpdate(RefinedStorageUtils.getIdFromHand(hand), direction, getSortingType(), getSearchBoxMode()));

        this.sortingDirection = direction;
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        RefinedStorage.NETWORK.sendToServer(new MessageWirelessGridSettingsUpdate(RefinedStorageUtils.getIdFromHand(hand), getSortingDirection(), getSortingType(), searchBoxMode));

        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public IRedstoneModeConfig getRedstoneModeConfig() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return System.currentTimeMillis() - lastUpdate < 1000;
    }
}
