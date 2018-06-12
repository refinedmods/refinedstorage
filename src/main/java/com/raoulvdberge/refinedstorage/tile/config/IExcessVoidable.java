package com.raoulvdberge.refinedstorage.tile.config;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;

public interface IExcessVoidable {
    static <T extends TileEntity & INetworkNodeProxy> TileDataParameter<Boolean, T> createParameter() {
        return new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> ((IExcessVoidable) t.getNode()).isVoidExcess(), (t, v) -> ((IExcessVoidable) t.getNode()).setVoidExcess(v));
    }

    boolean isVoidExcess();

    void setVoidExcess(boolean voidExcess);
}
