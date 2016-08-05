package refinedstorage.tile.config;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public interface IModeConfig {
    int WHITELIST = 0;
    int BLACKLIST = 1;

    static <T extends TileEntity> TileDataParameter createConfigParameter() {
        return TileDataManager.createParameter(DataSerializers.VARINT, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IModeConfig) tile).getMode();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                if (value == WHITELIST || value == BLACKLIST) {
                    ((IModeConfig) tile).setMode(value);
                }
            }
        });
    }

    void setMode(int mode);

    int getMode();
}
