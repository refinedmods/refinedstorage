package refinedstorage.tile.config;


import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import refinedstorage.RSUtils;
import refinedstorage.api.storage.AccessType;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public interface IAccessType {
    int READ = 0;
    int WRITE = 1;
    int READ_WRITE = 2;

    static <T extends TileEntity> TileDataParameter<Integer> createParameter() {
        return new TileDataParameter<>(DataSerializers.VARINT, READ_WRITE, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IAccessType) tile).getAccessType().getId();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                ((IAccessType) tile).setAccessType(RSUtils.getAccessType(value));
            }
        });
    }

    AccessType getAccessType();

    void setAccessType(AccessType accessType);
}