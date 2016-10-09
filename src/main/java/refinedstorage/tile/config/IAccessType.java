package refinedstorage.tile.config;


import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public interface IAccessType {
    int READ = 1;
    int WRITE = 2;
    int READ_WRITE = 3;


    static <T extends TileEntity>TileDataParameter<Integer> createParameter() {
        return new TileDataParameter<Integer>(DataSerializers.VARINT, READ_WRITE, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IAccessType) tile).getAccessType();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                if(value == READ || value == WRITE || value == READ_WRITE) {
                    ((IAccessType) tile).setAccessType(value);
                }

            }
        });
    }

    int getAccessType();

    void setAccessType(int accessType);
}
