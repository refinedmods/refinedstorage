package refinedstorage.tile.grid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

    ItemStack onItemPush(EntityPlayer player, ItemStack stack);

    void onHeldItemPush(boolean one);

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
