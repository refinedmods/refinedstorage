package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.text.ITextComponent;

public interface IStorageScreen {
    ITextComponent getTitle();

    TileDataParameter<Integer, ?> getTypeParameter();

    TileDataParameter<Integer, ?> getRedstoneModeParameter();

    TileDataParameter<Integer, ?> getCompareParameter();

    TileDataParameter<Integer, ?> getWhitelistBlacklistParameter();

    TileDataParameter<Integer, ?> getPriorityParameter();

    TileDataParameter<AccessType, ?> getAccessTypeParameter();

    long getStored();

    long getCapacity();
}
