package com.raoulvdberge.refinedstorage.tile.config;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.tileentity.TileEntity;

public interface IAccessType {
    static <T extends TileEntity & INetworkNodeProxy> TileDataParameter<AccessType, T> createParameter() {
        return new TileDataParameter<>(RSSerializers.ACCESS_TYPE_SERIALIZER, AccessType.INSERT_EXTRACT, t -> ((IAccessType) t.getNode()).getAccessType(), (t, v) -> ((IAccessType) t.getNode()).setAccessType(v));
    }

    AccessType getAccessType();

    void setAccessType(AccessType accessType);
}