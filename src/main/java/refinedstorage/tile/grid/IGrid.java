package refinedstorage.tile.grid;

import net.minecraft.util.math.BlockPos;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.block.EnumGridType;
import refinedstorage.tile.config.IRedstoneModeConfig;

public interface IGrid {
    EnumGridType getType();

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
