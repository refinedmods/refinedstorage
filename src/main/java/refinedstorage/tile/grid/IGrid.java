package refinedstorage.tile.grid;

import net.minecraft.util.math.BlockPos;
import refinedstorage.block.EnumGridType;
import refinedstorage.tile.ClientItem;
import refinedstorage.tile.config.IRedstoneModeConfig;

import java.util.List;

public interface IGrid {
    EnumGridType getType();

    List<ClientItem> getItems();

    void setItems(List<ClientItem> items);

    BlockPos getControllerPos();

    void onItemPush(int playerSlot, boolean one);

    void onItemPull(int id, int flags);

    int getSortingType();

    int getSortingDirection();

    int getSearchBoxMode();

    void onSortingTypeChanged(int type);

    void onSortingDirectionChanged(int direction);

    void onSearchBoxModeChanged(int searchBoxMode);

    void onCraftingRequested(int id, int quantity);

    IRedstoneModeConfig getRedstoneModeSetting();

    boolean isConnected();
}
