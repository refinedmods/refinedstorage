package com.raoulvdberge.refinedstorage.tile.config;

import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;

public interface IExcessVoidable {
    static <T extends TileEntity> TileDataParameter<Boolean> createParameter() {
        return new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, T>() {
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
