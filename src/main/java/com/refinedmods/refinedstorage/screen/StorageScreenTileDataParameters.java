package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import javax.annotation.Nullable;

public class StorageScreenTileDataParameters {
    @Nullable
    private final TileDataParameter<Integer, ?> typeParameter;
    @Nullable
    private final TileDataParameter<Integer, ?> redstoneModeParameter;
    @Nullable
    private final TileDataParameter<Integer, ?> exactModeParameter;
    @Nullable
    private final TileDataParameter<Integer, ?> whitelistBlacklistParameter;
    private final TileDataParameter<Integer, ?> priorityParameter;
    @Nullable
    private final TileDataParameter<AccessType, ?> accessTypeParameter;

    public StorageScreenTileDataParameters(@Nullable TileDataParameter<Integer, ?> typeParameter, @Nullable TileDataParameter<Integer, ?> redstoneModeParameter, @Nullable TileDataParameter<Integer, ?> exactModeParameter, @Nullable TileDataParameter<Integer, ?> whitelistBlacklistParameter, TileDataParameter<Integer, ?> priorityParameter, @Nullable TileDataParameter<AccessType, ?> accessTypeParameter) {
        this.typeParameter = typeParameter;
        this.redstoneModeParameter = redstoneModeParameter;
        this.exactModeParameter = exactModeParameter;
        this.whitelistBlacklistParameter = whitelistBlacklistParameter;
        this.priorityParameter = priorityParameter;
        this.accessTypeParameter = accessTypeParameter;
    }

    @Nullable
    public TileDataParameter<Integer, ?> getTypeParameter() {
        return typeParameter;
    }

    @Nullable
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return redstoneModeParameter;
    }

    @Nullable
    public TileDataParameter<Integer, ?> getExactModeParameter() {
        return exactModeParameter;
    }

    @Nullable
    public TileDataParameter<Integer, ?> getWhitelistBlacklistParameter() {
        return whitelistBlacklistParameter;
    }

    public TileDataParameter<Integer, ?> getPriorityParameter() {
        return priorityParameter;
    }

    @Nullable
    public TileDataParameter<AccessType, ?> getAccessTypeParameter() {
        return accessTypeParameter;
    }
}
