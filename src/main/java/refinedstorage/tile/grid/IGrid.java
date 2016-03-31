package refinedstorage.tile.grid;

import refinedstorage.block.EnumGridType;
import refinedstorage.tile.TileController;
import refinedstorage.tile.settings.IRedstoneModeSetting;

public interface IGrid {
    EnumGridType getType();

    TileController getController();

    int getSortingType();

    int getSortingDirection();

    int getSearchBoxMode();

    void onSortingTypeChanged(int type);

    void onSortingDirectionChanged(int direction);

    void onSearchBoxModeChanged(int searchBoxMode);

    IRedstoneModeSetting getRedstoneModeSetting();

    boolean isConnected();
}
