package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;

import javax.annotation.Nullable;

public class StorageScreenSynchronizationParameters {
    @Nullable
    private final BlockEntitySynchronizationParameter<Integer, ?> typeParameter;
    @Nullable
    private final BlockEntitySynchronizationParameter<Integer, ?> redstoneModeParameter;
    @Nullable
    private final BlockEntitySynchronizationParameter<Integer, ?> exactModeParameter;
    @Nullable
    private final BlockEntitySynchronizationParameter<Integer, ?> whitelistBlacklistParameter;
    private final BlockEntitySynchronizationParameter<Integer, ?> priorityParameter;
    @Nullable
    private final BlockEntitySynchronizationParameter<AccessType, ?> accessTypeParameter;

    public StorageScreenSynchronizationParameters(@Nullable BlockEntitySynchronizationParameter<Integer, ?> typeParameter, @Nullable BlockEntitySynchronizationParameter<Integer, ?> redstoneModeParameter, @Nullable BlockEntitySynchronizationParameter<Integer, ?> exactModeParameter, @Nullable BlockEntitySynchronizationParameter<Integer, ?> whitelistBlacklistParameter, BlockEntitySynchronizationParameter<Integer, ?> priorityParameter, @Nullable BlockEntitySynchronizationParameter<AccessType, ?> accessTypeParameter) {
        this.typeParameter = typeParameter;
        this.redstoneModeParameter = redstoneModeParameter;
        this.exactModeParameter = exactModeParameter;
        this.whitelistBlacklistParameter = whitelistBlacklistParameter;
        this.priorityParameter = priorityParameter;
        this.accessTypeParameter = accessTypeParameter;
    }

    @Nullable
    public BlockEntitySynchronizationParameter<Integer, ?> getTypeParameter() {
        return typeParameter;
    }

    @Nullable
    public BlockEntitySynchronizationParameter<Integer, ?> getRedstoneModeParameter() {
        return redstoneModeParameter;
    }

    @Nullable
    public BlockEntitySynchronizationParameter<Integer, ?> getExactModeParameter() {
        return exactModeParameter;
    }

    @Nullable
    public BlockEntitySynchronizationParameter<Integer, ?> getWhitelistBlacklistParameter() {
        return whitelistBlacklistParameter;
    }

    public BlockEntitySynchronizationParameter<Integer, ?> getPriorityParameter() {
        return priorityParameter;
    }

    @Nullable
    public BlockEntitySynchronizationParameter<AccessType, ?> getAccessTypeParameter() {
        return accessTypeParameter;
    }
}
