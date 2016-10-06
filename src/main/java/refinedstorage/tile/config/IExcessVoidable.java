package refinedstorage.tile.config;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public interface IExcessVoidable {
    public static final boolean VOID_EXCESS_OFF = false;
    public static final boolean VOID_EXCESS_ON = true;

    static <T extends TileEntity> TileDataParameter<Boolean> createParameter() {
        return new TileDataParameter<>(DataSerializers.BOOLEAN, VOID_EXCESS_OFF, new ITileDataProducer<Boolean, T>() {
            @Override
            public Boolean getValue(T tile) {
                return ((IExcessVoidable) tile).getVoidExcess();
            }
        }, new ITileDataConsumer<Boolean, T>() {
            @Override
            public void setValue(T tile, Boolean value) {
                ((IExcessVoidable) tile).setVoidExcess(value);
            }
        });
    }

    boolean getVoidExcess();

    void setVoidExcess(boolean voidExcess);
}
