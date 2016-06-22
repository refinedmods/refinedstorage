package refinedstorage.tile.grid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.block.EnumGridType;
import refinedstorage.tile.config.IRedstoneModeConfig;

import java.util.List;

public interface IGrid {
    EnumGridType getType();

    List<ItemStack> getItems();

    void setItems(List<ItemStack> items);

    BlockPos getNetworkPosition();

    IGridHandler getGridHandler();

    int getSortingType();

    int getSortingDirection();

    int getSearchBoxMode();

    void onSortingTypeChanged(int type);

    void onSortingDirectionChanged(int direction);

    void onSearchBoxModeChanged(int searchBoxMode);

    IRedstoneModeConfig getRedstoneModeConfig();

    boolean isConnected();
}
