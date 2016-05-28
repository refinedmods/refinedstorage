package refinedstorage.tile.grid;

import net.minecraft.util.math.BlockPos;
import refinedstorage.block.EnumGridType;
import refinedstorage.storage.ClientItemGroup;
import refinedstorage.tile.config.IRedstoneModeConfig;

import java.util.List;

public interface IGrid {
    EnumGridType getType();

    List<ClientItemGroup> getItemGroups();

    void setItemGroups(List<ClientItemGroup> groups);

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
