package refinedstorage.tile;

import net.minecraftforge.items.IItemHandler;
import refinedstorage.tile.data.TileDataParameter;

public interface IStorageGui {
    String getGuiTitle();

    IItemHandler getFilters();

    TileDataParameter<Integer> getRedstoneModeParameter();

    TileDataParameter<Integer> getCompareParameter();

    default boolean hasComparisonFor(int compare) {
        return true;
    }

    TileDataParameter<Integer> getFilterParameter();

    TileDataParameter<Integer> getPriorityParameter();

    int getStored();

    int getCapacity();
}
