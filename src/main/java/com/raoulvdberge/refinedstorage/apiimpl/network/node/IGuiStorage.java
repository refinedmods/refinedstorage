package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;

public interface IGuiStorage {
    String getGuiTitle();

    TileDataParameter<Integer, ?> getTypeParameter();

    TileDataParameter<Integer, ?> getRedstoneModeParameter();

    TileDataParameter<Integer, ?> getCompareParameter();

    TileDataParameter<Integer, ?> getFilterParameter();

    TileDataParameter<Integer, ?> getPriorityParameter();

    TileDataParameter<AccessType, ?> getAccessTypeParameter();

    TileDataParameter<Boolean, ?> getVoidExcessParameter();

    String getVoidExcessType();

    int getStored();

    int getCapacity();
}
