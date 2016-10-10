package refinedstorage.tile;

import refinedstorage.api.storage.AccessType;
import refinedstorage.tile.data.TileDataParameter;

public interface IStorageGui {
    String getGuiTitle();

    TileDataParameter<Integer> getTypeParameter();

    TileDataParameter<Integer> getRedstoneModeParameter();

    TileDataParameter<Integer> getCompareParameter();

    TileDataParameter<Integer> getFilterParameter();

    TileDataParameter<Integer> getPriorityParameter();

    TileDataParameter<AccessType> getAccessTypeParameter();

    TileDataParameter<Boolean> getVoidExcessParameter();

    String getVoidExcessType();

    int getStored();

    int getCapacity();
}
