package com.raoulvdberge.refinedstorage.tile.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.gui.grid.GridFilter;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IGrid {
    EnumGridType getType();

    BlockPos getNetworkPosition();

    IItemGridHandler getItemHandler();

    IFluidGridHandler getFluidHandler();

    String getGuiTitle();

    int getViewType();

    int getSortingType();

    int getSortingDirection();

    int getSearchBoxMode();

    void onViewTypeChanged(int type);

    void onSortingTypeChanged(int type);

    void onSortingDirectionChanged(int direction);

    void onSearchBoxModeChanged(int searchBoxMode);

    List<GridFilter> getFilters();

    ItemHandlerBasic getFilter();

    TileDataParameter<Integer> getRedstoneModeConfig();

    boolean isConnected();
}
