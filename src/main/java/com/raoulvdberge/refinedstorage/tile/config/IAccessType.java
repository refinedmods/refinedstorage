package com.raoulvdberge.refinedstorage.tile.config;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.tileentity.TileEntity;

public interface IAccessType {
    static <T extends TileEntity & IAccessType> TileDataParameter<AccessType> createParameter() {
        return new TileDataParameter<>(RSSerializers.ACCESS_TYPE_SERIALIZER, AccessType.EXTRACT_INSERT, new ITileDataProducer<AccessType, T>() {
            @Override
            public AccessType getValue(T tile) {
                return tile.getAccessType();
            }
        }, new ITileDataConsumer<AccessType, T>() {
            @Override
            public void setValue(T tile, AccessType value) {
                tile.setAccessType(value);
            }
        });
    }

    AccessType getAccessType();

    void setAccessType(AccessType accessType);
}