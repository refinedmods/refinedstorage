package refinedstorage.tile.config;

import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import refinedstorage.gui.GuiStorage;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public interface IPrioritizable {
    static <T extends TileEntity> TileDataParameter<Integer> createParameter() {
        return new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IPrioritizable) tile).getPriority();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                ((IPrioritizable) tile).setPriority(value);
            }
        }, parameter -> {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiStorage) {
                ((GuiStorage) Minecraft.getMinecraft().currentScreen).updatePriority(parameter.getValue());
            }
        });
    }

    int getPriority();

    void setPriority(int priority);
}
