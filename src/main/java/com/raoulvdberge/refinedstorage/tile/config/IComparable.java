package com.raoulvdberge.refinedstorage.tile.config;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;

public interface IComparable {
    static <T extends TileEntity & INetworkNodeProxy> TileDataParameter<Integer> createParameter() {
        return new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IComparable) tile.getNode()).getCompare();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                ((IComparable) tile.getNode()).setCompare(value);
            }
        });
    }

    int getCompare();

    void setCompare(int compare);
}
