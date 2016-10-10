package refinedstorage.tile.config;

import net.minecraft.tileentity.TileEntity;
import refinedstorage.api.storage.AccessType;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.RSSerializers;
import refinedstorage.tile.data.TileDataParameter;

public interface IAccessType {
    static <T extends TileEntity> TileDataParameter<AccessType> createParameter() {
        return new TileDataParameter<>(RSSerializers.ACCESS_TYPE_SERIALIZER, AccessType.READ_WRITE, new ITileDataProducer<AccessType, T>() {
            @Override
            public AccessType getValue(T tile) {
                return ((IAccessType) tile).getAccessType();
            }
        }, new ITileDataConsumer<AccessType, T>() {
            @Override
            public void setValue(T tile, AccessType value) {
                ((IAccessType) tile).setAccessType(value);
            }
        });
    }

    AccessType getAccessType();

    void setAccessType(AccessType accessType);
}