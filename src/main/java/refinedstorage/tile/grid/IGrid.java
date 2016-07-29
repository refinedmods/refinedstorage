package refinedstorage.tile.grid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.block.EnumGridType;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.tile.config.IRedstoneModeConfig;

import java.util.List;

public interface IGrid {
    EnumGridType getType();

    BlockPos getNetworkPosition();

    IGridHandler getGridHandler();

    int getViewType();

    int getSortingType();

    int getSortingDirection();

    int getSearchBoxMode();

    void onViewTypeChanged(int type);

    void onSortingTypeChanged(int type);

    void onSortingDirectionChanged(int direction);

    void onSearchBoxModeChanged(int searchBoxMode);

    List<ItemStack> getFilteredItems();

    BasicItemHandler getFilter();

    IRedstoneModeConfig getRedstoneModeConfig();

    boolean isConnected();
}
