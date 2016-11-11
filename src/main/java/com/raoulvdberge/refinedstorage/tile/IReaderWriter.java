package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;

public interface IReaderWriter {
    String getTitle();

    String getChannel();

    void setChannel(String channel);

    TileDataParameter<String> getChannelParameter();

    void onAdd(String name);

    void onRemove(String name);

    BlockPos getNetworkPosition();

    boolean isConnected();
}
