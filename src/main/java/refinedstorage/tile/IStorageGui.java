package refinedstorage.tile;

import net.minecraftforge.items.IItemHandler;
import refinedstorage.tile.data.TileDataParameter;

public interface IStorageGui {
    String getGuiTitle();

    int getPriority();

    void onPriorityChanged(int priority);

    IItemHandler getFilters();

    TileDataParameter<Integer> getRedstoneModeConfig();

    TileDataParameter<Integer> getCompareConfig();

    TileDataParameter<Integer> getModeConfig();

    int getStored();

    int getCapacity();
}
