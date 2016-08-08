package refinedstorage.tile.config;

import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import refinedstorage.gui.GuiStorage;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public interface IPrioritizable {
    static <T extends TileEntity> TileDataParameter<Integer> createParameter() {
        return TileDataManager.createParameter(DataSerializers.VARINT, new ITileDataProducer<Integer, T>() {
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
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && Minecraft.getMinecraft().currentScreen instanceof GuiStorage) {
                ((GuiStorage) Minecraft.getMinecraft().currentScreen).updatePriority(parameter.getValue());
            }
        }, 0);
    }

    int getPriority();

    void setPriority(int priority);
}
