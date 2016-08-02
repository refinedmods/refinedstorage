package refinedstorage.tile.grid;

import net.minecraft.util.math.BlockPos;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.block.EnumGridType;
import refinedstorage.gui.grid.GridFilteredItem;
import refinedstorage.inventory.ItemHandlerBasic;
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

    List<GridFilteredItem> getFilteredItems();

    ItemHandlerBasic getFilter();

    IRedstoneModeConfig getRedstoneModeConfig();

    boolean isConnected();
}
