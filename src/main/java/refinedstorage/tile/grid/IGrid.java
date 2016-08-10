package refinedstorage.tile.grid;

import net.minecraft.util.math.BlockPos;
import refinedstorage.api.network.grid.IItemGridHandler;
import refinedstorage.block.EnumGridType;
import refinedstorage.gui.grid.GridFilteredItem;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.tile.data.TileDataParameter;

import java.util.List;

public interface IGrid {
    EnumGridType getType();

    BlockPos getNetworkPosition();

    IItemGridHandler getGridHandler();

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

    TileDataParameter<Integer> getRedstoneModeConfig();

    boolean isConnected();
}
