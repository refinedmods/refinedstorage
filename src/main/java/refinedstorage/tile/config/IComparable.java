package refinedstorage.tile.config;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public interface IComparable {
    static <T extends TileEntity> TileDataParameter<Integer> createParameter() {
        return TileDataManager.createParameter(DataSerializers.VARINT, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IComparable) tile).getCompare();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                ((IComparable) tile).setCompare(value);
            }
        });
    }

    int getCompare();

    void setCompare(int compare);
}
