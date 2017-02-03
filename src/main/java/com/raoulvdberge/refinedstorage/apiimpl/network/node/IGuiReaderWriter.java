package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;

public interface IGuiReaderWriter {
    String getTitle();

    String getChannel();

    void setChannel(String channel);

    TileDataParameter<String> getChannelParameter();

    TileDataParameter<Integer> getRedstoneModeParameter();

    INetworkMaster getNetwork();

    boolean canUpdate();

    default void onAdd(String name) {
        INetworkMaster network = getNetwork();

        if (network != null && !name.isEmpty()) {
            network.addReaderWriterChannel(name);
        }
    }

    default void onRemove(String name) {
        INetworkMaster network = getNetwork();

        if (network != null && !name.isEmpty()) {
            network.removeReaderWriterChannel(name);
        }
    }
}
