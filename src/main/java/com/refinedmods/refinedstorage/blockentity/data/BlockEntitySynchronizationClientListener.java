package com.refinedmods.refinedstorage.blockentity.data;

public interface BlockEntitySynchronizationClientListener<T> {
    void onChanged(boolean initial, T value);
}
