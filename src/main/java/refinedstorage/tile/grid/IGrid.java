package refinedstorage.tile.grid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import refinedstorage.block.EnumGridType;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.controller.StorageHandler;

import java.util.List;

public interface IGrid {
    EnumGridType getType();

    List<ItemStack> getItems();

    void setItems(List<ItemStack> items);

    BlockPos getNetworkPosition();

    StorageHandler getStorageHandler();

    int getSortingType();

    int getSortingDirection();

    int getSearchBoxMode();

    void onSortingTypeChanged(int type);

    void onSortingDirectionChanged(int direction);

    void onSearchBoxModeChanged(int searchBoxMode);

    IRedstoneModeConfig getRedstoneModeConfig();

    boolean isConnected();
}
