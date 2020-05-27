package com.refinedmods.refinedstorage.tile.data;

public interface TileDataParameterClientListener<T> {
    void onChanged(boolean initial, T value);
}
