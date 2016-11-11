package com.raoulvdberge.refinedstorage.tile;

import net.minecraft.util.math.BlockPos;

public interface IReaderWriter {
    String getTitle();

    void onAdd(String name);

    void onRemove(String name);

    BlockPos getNetworkPosition();

    boolean isConnected();
}
