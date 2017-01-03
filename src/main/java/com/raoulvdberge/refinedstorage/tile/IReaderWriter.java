package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;

public interface IReaderWriter extends INetworkNode {
    String getTitle();

    String getChannel();

    void setChannel(String channel);

    TileDataParameter<String> getChannelParameter();

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
