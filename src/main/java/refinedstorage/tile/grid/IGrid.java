package refinedstorage.tile.grid;

import refinedstorage.block.EnumGridType;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.config.IRedstoneModeConfig;

import java.util.List;

public interface IGrid {
    EnumGridType getType();

    List<ItemGroup> getItemGroups();

    void onItemPush(int playerSlot, boolean one);

    void onItemPull(int id, int flags);

    int getSortingType();

    int getSortingDirection();

    int getSearchBoxMode();

    void onSortingTypeChanged(int type);

    void onSortingDirectionChanged(int direction);

    void onSearchBoxModeChanged(int searchBoxMode);

    IRedstoneModeConfig getRedstoneModeSetting();

    boolean isConnected();
}
