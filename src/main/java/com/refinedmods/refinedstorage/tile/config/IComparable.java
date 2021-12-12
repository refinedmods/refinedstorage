package com.refinedmods.refinedstorage.tile.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;

public interface IComparable {
    static <T extends TileEntity & INetworkNodeProxy<?>> TileDataParameter<Integer, T> createParameter() {
        return new TileDataParameter<>(DataSerializers.INT, 0, t -> ((IComparable) t.getNode()).getCompare(), (t, v) -> ((IComparable) t.getNode()).setCompare(v));
    }

    int getCompare();

    void setCompare(int compare);
}
