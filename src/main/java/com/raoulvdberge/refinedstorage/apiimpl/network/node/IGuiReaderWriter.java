package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;

public interface IGuiReaderWriter {
    String getTitle();

    String getChannel();

    void setChannel(String channel);

    TileDataParameter<String, ?> getChannelParameter();

    TileDataParameter<Integer, ?> getRedstoneModeParameter();

    INetwork getNetwork();

    boolean isActive();

    default void onAdd(String name) {
        INetwork network = getNetwork();

        if (network != null && !name.isEmpty()) {
            network.addReaderWriterChannel(name);
        }
    }

    default void onRemove(String name) {
        INetwork network = getNetwork();

        if (network != null && !name.isEmpty()) {
            network.removeReaderWriterChannel(name);
        }
    }
}
