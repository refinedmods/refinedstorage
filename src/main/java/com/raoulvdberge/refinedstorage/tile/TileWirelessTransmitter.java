package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeWirelessTransmitter;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;

public class TileWirelessTransmitter extends TileNode {
    public static final TileDataParameter<Integer> RANGE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileWirelessTransmitter>() {
        @Override
        public Integer getValue(TileWirelessTransmitter tile) {
            return ((NetworkNodeWirelessTransmitter) tile.getNode()).getRange();
        }
    });

    public TileWirelessTransmitter() {
        dataManager.addWatchedParameter(RANGE);
    }

    @Override
    public INetworkNode createNode() {
        return new NetworkNodeWirelessTransmitter(this);
    }
}
