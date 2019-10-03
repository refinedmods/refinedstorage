package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;

public interface IStorageScreen {
    String getGuiTitle();

    TileDataParameter<Integer, ?> getTypeParameter();

    TileDataParameter<Integer, ?> getRedstoneModeParameter();

    TileDataParameter<Integer, ?> getCompareParameter();

    TileDataParameter<Integer, ?> getWhitelistBlacklistParameter();

    TileDataParameter<Integer, ?> getPriorityParameter();

    TileDataParameter<AccessType, ?> getAccessTypeParameter();

    long getStored();

    long getCapacity();
}
